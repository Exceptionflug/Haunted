package de.exceptionflug.haunted.game.gate;

import de.exceptionflug.haunted.util.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Date: 02.08.2021
 *
 * @author Exceptionflug
 */
public final class MobGate {

    private final List<MobGateBlock> gateBlocks = new ArrayList<>();
    private final CuboidRegion gateRegion;
    private final CuboidRegion repairRegion;
    private final Location spawnLocation;
    private final String mapSection;
    private Player repairingPlayer;

    public MobGate(Location pos1, Location pos2, Location repairPos1, Location repairPos2, Location spawnLocation, String mapSection) {
        this.gateRegion = new CuboidRegion(pos1, pos2);
        this.repairRegion = new CuboidRegion(repairPos1, repairPos2);
        this.spawnLocation = spawnLocation;
        this.mapSection = mapSection;
        recordGateBlocks();
    }

    private void recordGateBlocks() {
        int minX = Math.min(gateRegion.pos1().getBlockX(), gateRegion.pos2().getBlockX());
        int minY = Math.min(gateRegion.pos1().getBlockY(), gateRegion.pos2().getBlockY());
        int minZ = Math.min(gateRegion.pos1().getBlockZ(), gateRegion.pos2().getBlockZ());
        int maxX = Math.max(gateRegion.pos1().getBlockX(), gateRegion.pos2().getBlockX());
        int maxY = Math.max(gateRegion.pos1().getBlockY(), gateRegion.pos2().getBlockY());
        int maxZ = Math.max(gateRegion.pos1().getBlockZ(), gateRegion.pos2().getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(gateRegion.pos1().getWorld(), x, y, z);
                    gateBlocks.add(new MobGateBlock(location, location.getBlock().getType()));
                }
            }
        }
    }

    public boolean broken() {
        return gateBlocks.stream().anyMatch(mobGateBlock -> mobGateBlock.broken);
    }

    public void damageGate(int damage) {
        for (int i = 0; i < damage; i++) {
            if (broken()) {
                return;
            }
            List<MobGateBlock> unbrokenBlocks = gateBlocks.stream().filter(mobGateBlock -> !mobGateBlock.broken).collect(Collectors.toList());
            MobGateBlock gateBlock = unbrokenBlocks.get(ThreadLocalRandom.current().nextInt(0, unbrokenBlocks.size()));
            gateBlock.location.getBlock().breakNaturally(null);
            gateBlock.broken = true;
        }
    }

    public void repairGate(int blocks) {
        for (int i = 0; i < blocks; i++) {
            if (repaired()) {
                return;
            }
            List<MobGateBlock> brokenBlocks = gateBlocks.stream().filter(mobGateBlock -> mobGateBlock.broken).collect(Collectors.toList());
            MobGateBlock gateBlock = brokenBlocks.get(ThreadLocalRandom.current().nextInt(0, brokenBlocks.size()));
            gateBlock.location.getBlock().setType(gateBlock.material);
            gateBlock.broken = false;
        }
    }

    private boolean repaired() {
        return gateBlocks.stream().noneMatch(mobGateBlock -> mobGateBlock.broken);
    }

    public boolean isInRepairZone(Location location) {
        return repairRegion.isInside(location);
    }

    public Player repairingPlayer() {
        return repairingPlayer;
    }

    public void repairingPlayer(Player repairingPlayer) {
        this.repairingPlayer = repairingPlayer;
    }

    public Location spawnLocation() {
        return spawnLocation;
    }

    public String mapSection() {
        return mapSection;
    }

    private static class MobGateBlock {

        private final Location location;
        private final Material material;
        private boolean broken;

        private MobGateBlock(Location location, Material material) {
            this.location = location;
            this.material = material;
        }

    }

}
