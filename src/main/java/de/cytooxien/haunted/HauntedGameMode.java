package de.cytooxien.haunted;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.handlers.HauntedRunningStateHandler;
import de.cytooxien.strider.common.game.GameInfo;
import de.cytooxien.strider.game.Game;
import de.cytooxien.strider.game.gamemode.GameMode;
import de.cytooxien.strider.game.gamemode.GameModeOption;

import java.util.concurrent.CompletableFuture;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedGameMode extends GameMode {

    @Override
    public void load() {
        getOptions().set(GameModeOption.INTERNAL_NAME, "ht");
        getOptions().set(GameModeOption.NAME, "Haunted");
        getOptions().set(GameModeOption.PREFIX, "§8[§6Haunted§8] ");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public CompletableFuture<? extends Game<?, ?, ?, ?>> createGame(GameInfo gameInfo) {
        return CompletableFuture.completedFuture(new HauntedGame(this, gameInfo));
    }

}
