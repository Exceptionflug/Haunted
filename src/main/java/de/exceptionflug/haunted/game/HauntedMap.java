package de.exceptionflug.haunted.game;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.section.MapSection;
import de.exceptionflug.haunted.util.CuboidRegion;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.map.AbstractGameMap;
import de.exceptionflug.projectvenom.game.map.teleporters.PerPlayerTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
public class HauntedMap extends AbstractGameMap {

    private final Map<Integer, AbstractWave> waveMap = new ConcurrentHashMap<>();
    private final List<MobGate> mobGates = new ArrayList<>();
    private final Map<String, MapSection> sections = new ConcurrentHashMap<>();
    private final GameContext context;

    public HauntedMap(String mapName, SpigotConfig config, GameContext context) {
        super(context, mapName, config, PerPlayerTeleporter.create(mapName, context));
        this.context = context;
    }

    @Override
    public void onSelect() {
        super.onSelect(); // Perform world load
        loadMapSections();
        loadMobGates();
        loadWaves();
    }

    private void loadWaves() {
        File folder = new File("plugins/Haunted/maps", internalName());
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            try {
                ConfiguredWave wave = new ConfiguredWave(file, context);
                waveMap.put(wave.wave(), wave);
            } catch (IOException | ParseException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Unable to load "+file.getName(), e);
            }
        }
    }

    private void loadMapSections() {
        for (String key : config().getKeys("sections")) {
            String displayName = config().getOrSetDefault("sections."+key+".displayName", "Region");
            List<CuboidRegion> regions = new ArrayList<>();
            for (String key2 : config().getKeys("sections."+key+".regions")) {
                Location pos1 = config().getLocation("sections."+key+".regions."+key2+".pos1");
                Location pos2 = config().getLocation("sections."+key+".regions."+key2+".pos2");
                regions.add(new CuboidRegion(pos1, pos2));
            }
            sections.put(key, new MapSection(key, displayName, regions));
        }
    }

    private void loadMobGates() {
        for (String key : config().getKeys("mobgates")) {
            Location pos1 = config().getLocation("mobgates."+key+".gate.pos1");
            Location pos2 = config().getLocation("mobgates."+key+".gate.pos2");
            Location repairPos1 = config().getLocation("mobgates."+key+".repairZone.pos1");
            Location repairPos2 = config().getLocation("mobgates."+key+".repairZone.pos2");
            Location spawnPos = config().getLocation("mobgates."+key+".spawn");
            MapSection section = mapSection(pos1);
            if (section == null) {
                Bukkit.getLogger().warning("Unable to load mob gate "+key+": pos1 must be inside of a map section!");
                continue;
            }
            mobGates.add(new MobGate(pos1, pos2, repairPos1, repairPos2, spawnPos, section.name()));
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

    public MapSection mapSection(Location location) {
        for (MapSection section : sections.values()) {
            if (section.isInside(location)) {
                return section;
            }
        }
        return null;
    }

    public MapSection mapSection(String name) {
        return sections.get(name);
    }

    public AbstractWave wave(int waveNumber) {
        return waveMap.get(waveNumber);
    }

    public List<MobGate> mobGates() {
        return mobGates;
    }
}
