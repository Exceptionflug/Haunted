package de.exceptionflug.haunted.game;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.section.MapSection;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.haunted.util.CuboidRegion;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.weapon.GunShop;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.map.AbstractGameMap;
import de.exceptionflug.projectvenom.game.map.teleporters.PerPlayerTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
public class HauntedMap extends AbstractGameMap {

    private final Map<Integer, Shop> shopMap = new ConcurrentHashMap<>();
    private final Map<Integer, AbstractWave> waveMap = new ConcurrentHashMap<>();
    private final List<MobGate> mobGates = new ArrayList<>();
    private final Map<String, SectionGate> sectionGates = new ConcurrentHashMap<>();
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
        loadSectionGates();
        loadShops();
        loadWaves();
    }

    private void loadShops() {
        int id = 0;
        for (String key : config().getKeys("shops")) {
            id ++;
            Location location = config().getLocation("shops." + key + ".location");
            String type = config().getOrSetDefault("shops." + key + ".type", "GUN");
            int price = config().getOrSetDefault("shops." + key + ".price", 100);
            Shop shop;
            if (type.equals("GUN")) {
                shop = new GunShop(id, GunType.valueOf(config().getOrSetDefault("shops." + key + ".gunType", "P90")),
                        price, price, location);
            } else {
                Bukkit.getLogger().warning("Unable to load shop of type " + type);
                continue;
            }
            shopMap.put(id, shop);
            shop.spawn();
        }
    }

    private void loadSectionGates() {
        for (String key : config().getKeys("sectiongates")) {
            Location pos1 = config().getLocation("sectiongates."+key+".gate.pos1");
            Location pos2 = config().getLocation("sectiongates."+key+".gate.pos2");
            Location hologram = config().getLocation("sectiongates."+key+".hologram");
            int price = config().getOrSetDefault("sectiongates."+key+".price", 1000);
            Set<String> materials = new HashSet<>(config().getOrSetDefault("sectiongates."+key+".materials", Collections.singletonList(Material.IRON_BLOCK.name())));
            String displayName = config().getOrSetDefault("sectiongates."+key+".displayName", "Bill Gates");
            sectionGates.put(key, new SectionGate(new CuboidRegion(pos1, pos2), materials, hologram, displayName, price));
        }
        Bukkit.getLogger().info("Loaded "+mobGates.size()+" mob gates");
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

    public MobGate mobGateByGateBlock(Location location) {
        for (MobGate mobGate : mobGates) {
            if (mobGate.isGateBlock(location)) {
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

    public SectionGate sectionGate(Location location) {
        for (SectionGate section : sectionGates.values()) {
            if (section.isGateBlock(location)) {
                return section;
            }
        }
        return null;
    }

    public SectionGate sectionGate(String name) {
        return sectionGates.get(name);
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

    public Collection<SectionGate> sectionGates() {
        return sectionGates.values();
    }

    public void reloadWaves() {
        loadWaves();
    }
}
