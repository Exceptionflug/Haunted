package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Date: 04.08.2021
 *
 * @author Exceptionflug
 */
@Component
public final class ToggleSneakListener implements Listener {

    private final GameContext game;

    @Inject
    public ToggleSneakListener(GameContext game) {
        this.game = game;
    }

    @EventHandler
    public void onToggle(PlayerToggleSneakEvent event) {
        MobGate mobGate = game.<HauntedMap>currentMap().mobGateByRepairZone(event.getPlayer().getLocation());
        if (mobGate != null) {
            Bukkit.getScheduler().runTaskLater(game.plugin(), () -> {
                if (event.getPlayer().isSneaking()) {
                    if (mobGate.repairingPlayer() != null) {
                        event.getPlayer().sendMessage("§cDieses Tor wird bereits repariert!");
                    } else {
                        mobGate.repairingPlayer(event.getPlayer());
                    }
                } else {
                    if (mobGate.repairingPlayer() != null) {
                        if (mobGate.repairingPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                            mobGate.repairingPlayer(null);
                        }
                    }
                }
            }, 1);
        }
    }

}
