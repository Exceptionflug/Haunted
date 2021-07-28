package de.cytooxien.haunted.handlers;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.game.HauntedPlayer;
import de.cytooxien.haunted.game.HauntedTeam;
import de.cytooxien.strider.game.state.GameStateOption;
import de.cytooxien.strider.game.state.handler.RunningStateHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedRunningStateHandler extends RunningStateHandler<HauntedGame, HauntedPlayer> {

    private HauntedTeam playerTeam;

    public HauntedRunningStateHandler(HauntedGame game) {
        super(game, (int) TimeUnit.HOURS.toSeconds(2));

        getOptions().set(GameStateOption.PVP, false);
        getOptions().set(GameStateOption.NO_DAMAGE, false);
        getOptions().set(GameStateOption.HUNGER, false);
        getOptions().set(GameStateOption.INTERACT, true);
        getOptions().set(GameStateOption.CRAFTING, false);
        getOptions().set(GameStateOption.ITEM_DROP, false);
        getOptions().set(GameStateOption.ITEM_PICKUP, false);

        getRunningStateOptions().set(StateOption.ANNOUNCE_DEATHS, true);
    }

    @Override
    public void enable() {
        super.enable();
        playerTeam = getGame().getTeamManager().getTeam(0);
    }

    @Override
    public void countdownTick() {
        super.countdownTick();
        getGame().getPlayerManager().getPlayingPlayers().forEach(HauntedPlayer::gameTick);
        if (!playerTeam.alive()) {
            end(false);
        }
    }

    @Override
    public void playerDied(PlayerDeathEvent event, HauntedPlayer player, HauntedPlayer killer) {
        super.playerDied(event, player, killer);
        player.playerDied();
    }

    private void end(boolean win) {
        HauntedEndStateHandler stateHandler = new HauntedEndStateHandler(getGame(), win);
        if (win) {
            stateHandler.setWinningTeam(getGame().getTeamManager().getTeam(0));
        }
        getGame().getStateManager().setCurrentHandler(stateHandler);
    }

}
