package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.haunted.switches.ElectricitySwitch;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
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
@Singleton
public final class PlayerInteractListener implements Listener {

    private final GameContext context;
    private final InternationalizationContext i18nContext;

    @Inject
    public PlayerInteractListener(GameContext context, InternationalizationContext i18nContext) {
        this.context = context;
        this.i18nContext = i18nContext;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!context.phase().ingamePhase()) return;
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
            i18nContext.broadcast(context.bukkitPlayers(), "Messages.gateOpened", c -> {
                c.setDefaultMessage(() -> "§6%player% §7hat das Tor %gate% §7geöffnet");
                c.setArgument("player", player.handle().getName());
                c.setArgument("gate", sectionGate.displayName());
            });
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
                    i18nContext.sendMessage(player.handle(), "Messages.canNotAfford", c -> {
                        c.setDefaultMessage(() -> "§cDu kannst dir das nicht leisten!");
                    });
                    return;
                }
                player.gold(player.gold() - electricitySwitch.price());
                electricitySwitch.pulled(true);
                electricitySwitch.hologram().disable();
                context.<HauntedIngamePhase>phase().electricity(player, true);
            }
        }
    }

}
