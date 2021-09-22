package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.DebugUtil;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.haunted.switches.ElectricitySwitch;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Date: 17.08.2021
 *
 * @author Exceptionflug
 */
@Component
public final class PlayerInteractListener implements Listener {

    private final GameContext context;

    @Inject
    public PlayerInteractListener(GameContext context) {
        this.context = context;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        HauntedPlayer player = context.player(event.getPlayer());
        if (player.spectator() || !context.phase().ingamePhase()) {
            event.setCancelled(true);
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        SectionGate sectionGate = context.<HauntedMap>currentMap().sectionGate(clickedBlock.getLocation());
        if (sectionGate != null) {
            if (sectionGate.price() > player.gold()) {
                return;
            }
            if (sectionGate.requiresPower() && !context.<HauntedIngamePhase>phase().electricity()) {
                return;
            }
            player.gold(player.gold() - sectionGate.price());
            sectionGate.unlock();
            Message.broadcast(context.players(), context.messageConfiguration(), "Messages.gateOpened", "§6%player% §7hat das Tor %gate% §7geöffnet", "%player%", player.getName(), "%gate%", sectionGate.displayName());
        }
        if (Tag.BUTTONS.isTagged(clickedBlock.getType())) {
            Shop shop = context.<HauntedMap>currentMap().shopByTrigger(clickedBlock.getLocation());
            if (shop == null) {
                return;
            }
            if (!shop.interact(player)) {
                event.setCancelled(true);
            }
        }
        if (clickedBlock.getType() == Material.LEVER) {
            ElectricitySwitch electricitySwitch = context.<HauntedMap>currentMap().electricitySwitch();
            if (electricitySwitch == null) {
                return;
            }
            if (clickedBlock.getLocation().getBlockX() == electricitySwitch.leverLocation().getBlockX() &&
                    clickedBlock.getLocation().getBlockY() == electricitySwitch.leverLocation().getBlockY() &&
                    clickedBlock.getLocation().getBlockZ() == electricitySwitch.leverLocation().getBlockZ()) {
                if (electricitySwitch.pulled()) {
                    event.setCancelled(true);
                    return;
                }
                if (player.gold() < electricitySwitch.price()) {
                    event.setCancelled(true);
                    Message.send(player, player.context().messageConfiguration(), "Messages.canNotAfford", "§cDu kannst dir das nicht leisten!");
                    return;
                }
                player.gold(player.gold() - electricitySwitch.price());
                electricitySwitch.pulled(true);
                electricitySwitch.hologram().despawn();
                context.<HauntedIngamePhase>phase().electricity(player, true);
            }
        }
    }

}
