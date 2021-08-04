package de.cytooxien.haunted.handlers;

import de.cytooxien.haunted.game.HauntedGame;
import de.cytooxien.haunted.game.HauntedPlayer;
import de.cytooxien.haunted.game.HauntedTeam;
import de.cytooxien.haunted.game.gate.MobGate;
import de.cytooxien.haunted.listeners.ToggleSneakListener;
import de.cytooxien.strider.game.state.GameStateOption;
import de.cytooxien.strider.game.state.handler.RunningStateHandler;
import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedRunningStateHandler extends RunningStateHandler<HauntedGame, HauntedPlayer> {

    private final List<MobGate> mobGates = new ArrayList<>();
    private HauntedTeam playerTeam;
    private Yaml mapConfig;

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

        getGame().getEventManager().registerEvents(new ToggleSneakListener(getGame()));
    }

    @Override
    public void enable() {
        super.enable();
        playerTeam = getGame().getTeamManager().getTeam(0);
        mapConfig = new Yaml(new File(getGameWorld().getFolder(), "config.yml"));
        loadMobGates();
    }

    private void loadMobGates() {
        for (String key : mapConfig.keySet("mobgates")) {
            Location pos1 = getConfigLocation("mobgates."+key+".gate.pos1");
            Location pos2 = getConfigLocation("mobgates."+key+".gate.pos2");
            Location repairPos1 = getConfigLocation("mobgates."+key+".repairZone.pos1");
            Location repairPos2 = getConfigLocation("mobgates."+key+".repairZone.pos2");
            mobGates.add(new MobGate(pos1, pos2, repairPos1, repairPos2));
        }
        Bukkit.getLogger().info("Loaded "+mobGates.size()+" mob gates");
    }

    public MobGate mobGateByRepairZone(Location location) {
        for (MobGate mobGate : mobGates) {
            if (mobGate.isInRepairZone(location)) {
                return mobGate;
            }
        }
        return null;
    }

    public Location getConfigLocation(String path) {
        double x = mapConfig.getOrSetDefault(path+".x", 0);
        double y = mapConfig.getOrSetDefault(path+".y", 0);
        double z = mapConfig.getOrSetDefault(path+".z", 0);
        Location out = new Location(getGameWorld().asWorld(), x, y, z);
        if (mapConfig.contains(path+".yaml")) {
            out.setYaw(mapConfig.getOrSetDefault(path+".yaw", 0));
        }
        if (mapConfig.contains(path+".pitch")) {
            out.setPitch(mapConfig.getOrSetDefault(path+".pitch", 0));
        }
        return out;
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
