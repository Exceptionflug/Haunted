package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
@Component
public final class PlayerDeathListener implements Listener {

    private final GameContext gameContext;

    @Inject
    public PlayerDeathListener(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);
        //event.setShouldDropExperience(false);
        //event.setCancelled(true);
        HauntedPlayer player = gameContext.player(event.getEntity());
        if (player != null) {
            player.playerDied();
        }
        if (gameContext.alivePlayers().size() == 0) {
            HauntedIngamePhase phase = gameContext.phase();
            phase.endGame();
        }
    }

}
