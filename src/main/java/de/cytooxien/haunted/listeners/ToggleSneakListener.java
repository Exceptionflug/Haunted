package de.cytooxien.haunted.listeners;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.game.gate.MobGate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Date: 04.08.2021
 *
 * @author Exceptionflug
 */
public final class ToggleSneakListener implements Listener {

    private final HauntedGame game;

    public ToggleSneakListener(HauntedGame game) {
        this.game = game;
    }

    @EventHandler
    public void onToggle(PlayerToggleSneakEvent event) {
        MobGate mobGate = game.runningStateHandler().mobGateByRepairZone(event.getPlayer().getLocation());
        if (mobGate != null) {
            game.getScheduler().runLaterSync(() -> {
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
