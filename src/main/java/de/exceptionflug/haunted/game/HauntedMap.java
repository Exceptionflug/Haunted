package de.exceptionflug.haunted.game;

import de.exceptionflug.haunted.armor.ArmorShop;
import de.exceptionflug.haunted.armor.ArmorType;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.perk.PerkShop;
import de.exceptionflug.haunted.perk.PerkType;
import de.exceptionflug.haunted.section.MapSection;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.haunted.switches.ElectricitySwitch;
import de.exceptionflug.haunted.util.CuboidRegion;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.weapon.GunShop;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.map.AbstractGameMap;
import de.exceptionflug.projectvenom.game.map.teleporters.PerPlayerTeleporter;
import de.leonhard.storage.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Haunted: Map")
public class HauntedMap extends AbstractGameMap {

    private final Map<Integer, Shop> shopMap = new ConcurrentHashMap<>();
    private final Map<Integer, AbstractWave> waveMap = new ConcurrentHashMap<>();
    private final List<MobGate> mobGates = new ArrayList<>();
    private final Map<String, SectionGate> sectionGates = new ConcurrentHashMap<>();
    private final Map<String, MapSection> sections = new ConcurrentHashMap<>();
    private final GameContext context;
    private ElectricitySwitch electricitySwitch;

    public HauntedMap(String mapName, Yaml config, GameContext context) {
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
        loadSwitch();
        loadWaves();
    }

    private void loadSwitch() {
        if (config().contains("switch.location")) {
            return;
        }
        electricitySwitch = new ElectricitySwitch(locationFromConfig("switch.location"),
                locationFromConfig("switch.leverLocation"),
                config().getOrSetDefault("switch.name", "Generator"),
                config().getOrSetDefault("switch.price", 5000));
        electricitySwitch.spawn();
    }

    private void loadShops() {
        int id = 0;
        for (String key : config().singleLayerKeySet("shops")) {
            id++;
            Location location = locationFromConfig("shops." + key + ".location");
            Location buttonLocation = locationFromConfig("shops." + key + ".buttonLocation");
            String type = config().getOrSetDefault("shops." + key + ".type", "GUN");
            Shop shop;
            if (type.equals("GUN")) {
                GunType gunType = GunType.valueOf(config().getOrSetDefault("shops." + key + ".gunType", "P90"));
                int price = config().getOrSetDefault("shops." + key + ".price", 100);
                shop = new GunShop(id, gunType, price, price, location, buttonLocation);
            } else if (type.equals("PERK")) {
                PerkType perkType = PerkType.valueOf(config().getOrSetDefault("shops." + key + ".perkType", "HEAL"));
                List<Integer> prices = new ArrayList<>();
                for (String level : config().singleLayerKeySet("shops." + key + ".prices")) {
                    prices.add(config().getOrSetDefault("shops." + key + ".prices." + level, 100));
                }
                shop = new PerkShop(id, perkType, prices, location, buttonLocation);
            } else if (type.equals("ARMOR")) {
                String variant = config().getOrSetDefault("shops." + key + ".variant", "SINGLE");
                if (variant.equals("SINGLE")) {
                    ArmorType armorType = ArmorType.valueOf(config().getOrSetDefault("shops." + key + ".armorType", "LEATHER"));
                    int price = config().getOrSetDefault("shops." + key + ".price", 100);
                    shop = new ArmorShop(id, armorType, price, location, buttonLocation);
                } else if (variant.equals("MULTIPLE")) {
                    //double priceMultiplier = config().getOrSetDefault("shops." + key + ".priceMultiplier", 1.5);
                    //List<List<String>> armorTypes = config().getOrSetDefault("shops." + key + ".armorTypes", List.of(List.of("LEATHER")));
                    //shop = new MultipleArmorShop(id, armorTypes.stream().map(l -> l.stream().map(ArmorType::valueOf).collect(Collectors.toList())).collect(Collectors.toList()),
                    //        price, priceMultiplier, location, buttonLocation);
                    shop = null;
                } else {
                    log.warn("Unable to load ARMOR shop of variant {}", variant);
                    continue;
                }
            } else {
                log.warn("Unable to load shop of type {}", type);
                continue;
            }
            shopMap.put(id, shop);
            shop.spawn();
        }
    }

    private void loadSectionGates() {
        for (String key : config().singleLayerKeySet("sectiongates")) {
            Location pos1 = locationFromConfig("sectiongates." + key + ".gate.pos1");
            Location pos2 = locationFromConfig("sectiongates." + key + ".gate.pos2");
            Location hologram = locationFromConfig("sectiongates." + key + ".hologram");
            Location hologram2 = null;
            if (config().contains("sectiongates." + key + ".hologram2")) {
                hologram2 = locationFromConfig("sectiongates." + key + ".hologram2");
            }
            int price = config().getOrSetDefault("sectiongates." + key + ".price", 1000);
            boolean requiresPower = config().getOrSetDefault("sectiongates." + key + ".requiresPower", false);
            Set<String> materials = new HashSet<>(config().getOrSetDefault("sectiongates." + key + ".materials", Collections.singletonList(Material.IRON_BLOCK.name())));
            String displayName = config().getOrSetDefault("sectiongates." + key + ".displayName", "Bill Gates");
            sectionGates.put(key, new SectionGate(new CuboidRegion(pos1, pos2), materials, hologram, hologram2, displayName, price, requiresPower));
        }
        log.info("Loaded {} mob gates", mobGates.size());
    }

    private void loadWaves() {
        File folder = new File("plugins/Haunted/maps", internalName());
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            try {
                ConfiguredWave wave = new ConfiguredWave(file, context);
                waveMap.put(wave.wave(), wave);
            } catch (IOException | ParseException e) {
                log.error("Unable to load {}", file.getName(), e);
            }
        }
    }

    private void loadMapSections() {
        for (String key : config().singleLayerKeySet("sections")) {
            String displayName = config().getOrSetDefault("sections." + key + ".displayName", "Region");
            List<CuboidRegion> regions = new ArrayList<>();
            for (String key2 : config().singleLayerKeySet("sections." + key + ".regions")) {
                Location pos1 = locationFromConfig("sections." + key + ".regions." + key2 + ".pos1");
                Location pos2 = locationFromConfig("sections." + key + ".regions." + key2 + ".pos2");
                regions.add(new CuboidRegion(pos1, pos2));
            }
            sections.put(key, new MapSection(key, displayName, regions));
        }
    }

    private void loadMobGates() {
        for (String key : config().singleLayerKeySet("mobgates")) {
            Location pos1 = locationFromConfig("mobgates." + key + ".gate.pos1");
            Location pos2 = locationFromConfig("mobgates." + key + ".gate.pos2");
            Location repairPos1 = locationFromConfig("mobgates." + key + ".repairZone.pos1");
            Location repairPos2 = locationFromConfig("mobgates." + key + ".repairZone.pos2");
            Location spawnPos = locationFromConfig("mobgates." + key + ".spawn");
            MapSection section = mapSection(pos1);
            if (section == null) {
                log.warn("Unable to load mob gate {}: pos1 must be inside of a map section!", key);
                continue;
            }
            mobGates.add(new MobGate(pos1, pos2, repairPos1, repairPos2, spawnPos, section.name()));
        }
        log.warn("Loaded {} mob gates", mobGates.size());
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

    public Shop shopByTrigger(Location location) {
        for (Shop shop : shopMap.values()) {
            if (shop.triggerLocation().getBlockX() == location.getBlockX() &&
                    shop.triggerLocation().getBlockY() == location.getBlockY() &&
                    shop.triggerLocation().getBlockZ() == location.getBlockZ()) {
                return shop;
            }
        }
        return null;
    }

    public ElectricitySwitch electricitySwitch() {
        return electricitySwitch;
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

    public Collection<Shop> shops() {
        return shopMap.values();
    }

    public Collection<MapSection> sections() {
        return sections.values();
    }
}
