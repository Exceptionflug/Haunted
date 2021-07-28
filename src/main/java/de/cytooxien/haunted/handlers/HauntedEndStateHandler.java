package de.cytooxien.haunted.handlers;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.game.HauntedPlayer;
import de.cytooxien.strider.game.state.handler.EndStateHandler;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedEndStateHandler extends EndStateHandler<HauntedGame, HauntedPlayer> {

    private final boolean win;

    public HauntedEndStateHandler(HauntedGame game, boolean win) {
        super(game, 15);
        this.win = win;

        getEndStateOptions().set(StateOption.MUSIC, true);
    }

    @Override
    public void enable() {
        super.enable();

        if (win) {
            getWinningTeam().getMembers().forEach(player -> {
                if (player.isOnline()) {
                    // TODO Stats
                }
            });
        }
    }
}
