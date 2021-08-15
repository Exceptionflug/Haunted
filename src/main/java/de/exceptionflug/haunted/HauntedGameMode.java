package de.exceptionflug.haunted;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.phases.HauntedLobbyPhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.GameContextBuilder;
import de.exceptionflug.projectvenom.game.behaviours.RoundBasedGameBehaviour;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedGameMode extends JavaPlugin {

    private GameContext context;

    @Override
    public void onEnable() {
        context = GameContextBuilder.create(this)
                .behaviour(RoundBasedGameBehaviour.create(HauntedIngamePhase.class, HauntedLobbyPhase.class))
                .gamePlayerFactory(HauntedPlayer::new)
                .gameMapFactory(HauntedMap::new)
                .createGameContext();
    }

}