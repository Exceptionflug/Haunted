package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.mccommons.config.spigot.Message;
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
        processMobGate(event);
        processLayingBody(event);
    }

    private void processLayingBody(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            if (!game.phase().ingamePhase()) {
                return;
            }
            HauntedIngamePhase phase = game.phase();
            HauntedPlayer player = game.player(event.getPlayer());
            if (player.spectator()) {
                return;
            }
            HauntedPlayer dead = phase.deadBodyInRange(player.getLocation(), 3);
            if (dead == null) {
                return;
            }
            dead.revive(player);
        }
    }

    private void processMobGate(PlayerToggleSneakEvent event) {
        HauntedPlayer player = game.player(event.getPlayer());
        if (player.spectator()) {
            return;
        }
        MobGate mobGate = game.<HauntedMap>currentMap().mobGateByRepairZone(event.getPlayer().getLocation());
        if (mobGate != null) {
            if (!game.phase().ingamePhase()) {
                return;
            }
            Bukkit.getScheduler().runTaskLater(game.plugin(), () -> {
                if (event.getPlayer().isSneaking() && !mobGate.repaired()) {
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
