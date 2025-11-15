package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
@Singleton
@Slf4j(topic = "Haunted: PlayerDeathListener")
public final class PlayerDeathListener implements Listener {

    private final GameContext gameContext;

    @Inject
    public PlayerDeathListener(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        try {
            event.getDrops().clear();
            event.deathMessage(null);
            event.setShouldDropExperience(false);
            event.setCancelled(true);
            HauntedPlayer player = gameContext.player(event.getEntity());
            if (player != null) {
                player.playerDied();
            }
            if (gameContext.alivePlayers().isEmpty()) {
                HauntedIngamePhase phase = gameContext.phase();
                phase.endGame();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
