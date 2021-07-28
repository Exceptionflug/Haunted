package de.cytooxien.haunted.handlers;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.game.HauntedPlayer;
import de.cytooxien.strider.game.state.handler.LobbyStateHandler;
import de.cytooxien.strider.game.world.GameWorld;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedLobbyStateHandler extends LobbyStateHandler<HauntedGame, HauntedPlayer> {

    public HauntedLobbyStateHandler(HauntedGame game, int maxDuration) {
        super(game, maxDuration);

        getLobbyStateOptions().set(StateOption.TIME_BOSS_BAR, true);
    }

    @Override
    public void countdownEnded() {
        super.countdownEnded();
        getGame().loadMap(GameWorld::loadWithVoidGenerator, (gameWorld, world) -> {}, true, () -> {
            getGame().getStateManager().setCurrentHandler(new HauntedRunningStateHandler(getGame()));
        });
    }
}
