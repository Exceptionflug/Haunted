package de.cytooxien.haunted.game;

import de.cytooxien.haunted.HauntedGameMode;
import de.cytooxien.haunted.handlers.HauntedLobbyStateHandler;
import de.cytooxien.stats.api.spigot.SpigotStatsHopper;
import de.cytooxien.stats.api.spigot.StatsManager;
import de.cytooxien.strider.common.game.GameInfo;
import de.cytooxien.strider.common.map.GameMapInfo;
import de.cytooxien.strider.game.Game;

import java.util.UUID;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedGame extends Game<HauntedGameMode, HauntedPlayer, HauntedTeam, HauntedGame> {

    private SpigotStatsHopper statsHopper;

    public HauntedGame(HauntedGameMode gameMode, GameInfo gameInfo) {
        super(gameMode, gameInfo);
        getStateManager().setCurrentHandler(new HauntedLobbyStateHandler(this, 60));
    }

    @Override
    public void init(GameMapInfo mapInfo) {
        super.init(mapInfo);
        this.statsHopper = StatsManager.getInstance().getStatsHopper("haunted");
    }

    @Override
    public HauntedPlayer createPlayer(UUID uuid) {
        return new HauntedPlayer(this, uuid);
    }

    @Override
    public HauntedTeam createTeam(int i) {
        return new HauntedTeam(this, i);
    }

    @Override
    public HauntedGame getInstance() {
        return this;
    }

    @Override
    public String getDescription() {
        return getMap().getDisplayName();
    }

    @Override
    public SpigotStatsHopper getStatsHopper() {
        return statsHopper;
    }
}
