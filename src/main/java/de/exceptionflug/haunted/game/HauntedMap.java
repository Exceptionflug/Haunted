package de.exceptionflug.haunted.game;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.map.AbstractGameMap;
import de.exceptionflug.projectvenom.game.map.teleporters.PerPlayerTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
public class HauntedMap extends AbstractGameMap {

    private final List<MobGate> mobGates = new ArrayList<>();

    public HauntedMap(String mapName, SpigotConfig config, GameContext context) {
        super(context, mapName, config, PerPlayerTeleporter.create(mapName, context));
    }

    @Override
    public void onSelect() {
        super.onSelect(); // Perform world load
        loadMobGates();
    }

    private void loadMobGates() {
        for (String key : config().getKeys("mobgates")) {
            Location pos1 = config().getLocation("mobgates."+key+".gate.pos1");
            Location pos2 = config().getLocation("mobgates."+key+".gate.pos2");
            Location repairPos1 = config().getLocation("mobgates."+key+".repairZone.pos1");
            Location repairPos2 = config().getLocation("mobgates."+key+".repairZone.pos2");
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

}
