package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.DebugUtil;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.swing.*;

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
        if (player.spectator()) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedBlock() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        SectionGate sectionGate = context.<HauntedMap>currentMap().sectionGate(event.getClickedBlock().getLocation());
        if (sectionGate != null) {
            if (sectionGate.price() > player.gold()) {
                return;
            }
            player.gold(player.gold() - sectionGate.price());
            sectionGate.unlock();
            Message.broadcast(context.players(), context.messageConfiguration(), "Messages.gateOpened", "§6%player% §7hat das Tor %gate% §7geöffnet", "%player%", player.getName(), "%gate%", sectionGate.displayName());
        }
    }

}
