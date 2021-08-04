package de.cytooxien.haunted.game.gate;

import org.bukkit.Location;
import org.bukkit.Material;

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
    private final Location pos1;
    private final Location pos2;
    private final Location repairPos1;
    private final Location repairPos2;

    public MobGate(Location pos1, Location pos2, Location repairPos1, Location repairPos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.repairPos1 = repairPos1;
        this.repairPos2 = repairPos2;
        recordGateBlocks();
    }

    private void recordGateBlocks() {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(pos1.getWorld(), x, y, z);
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
        int minX = Math.min(repairPos1.getBlockX(), repairPos2.getBlockX());
        int minY = Math.min(repairPos1.getBlockY(), repairPos2.getBlockY());
        int minZ = Math.min(repairPos1.getBlockZ(), repairPos2.getBlockZ());
        int maxX = Math.max(repairPos1.getBlockX(), repairPos2.getBlockX());
        int maxY = Math.max(repairPos1.getBlockY(), repairPos2.getBlockY());
        int maxZ = Math.max(repairPos1.getBlockZ(), repairPos2.getBlockZ());
        return location.getBlockX() >= minX && location.getBlockX() <= maxX &&
                location.getBlockY() >= minY && location.getBlockY() <= maxY &&
                location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
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
