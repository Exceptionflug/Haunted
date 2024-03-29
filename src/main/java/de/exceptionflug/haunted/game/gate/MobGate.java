package de.exceptionflug.haunted.game.gate;

import de.exceptionflug.haunted.util.CuboidRegion;
import de.exceptionflug.haunted.util.HighlightUtil;
import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
                    Block block = location.getBlock();
                    gateBlocks.add(new MobGateBlock(location, block.getType(), block.getBlockData().clone()));
                }
            }
        }
    }

    private MobGateBlock getGateBlock(Location location) {
        for (MobGateBlock gateBlock : gateBlocks) {
            if (gateBlock.location.getBlockX() == location.getBlockX()
                && gateBlock.location.getBlockY() == location.getBlockY()
                && gateBlock.location.getBlockZ() == location.getBlockZ()) return gateBlock;
        }
        return null;
    }

    public boolean damaged() {
        return gateBlocks.stream().anyMatch(mobGateBlock -> mobGateBlock.broken);
    }

    public boolean broken() {
        return gateBlocks.stream().allMatch(mobGateBlock -> mobGateBlock.broken);
    }

    public void damageGate(int damage) {
        for (int i = 0; i < damage; i++) {
            if (broken()) {
                return;
            }
            //MobGateBlock gateBlock = getDamageableGateBlock();
            //gateBlock.location.getBlock().breakNaturally(null);
            //gateBlock.broken = true;
        }
    }

    public Location getFirstGateBlock() {
        return gateBlocks.get(0).location;
    }

    public Location getDamageableGateBlock() {
        List<MobGateBlock> unbrokenBlocks = gateBlocks.stream().filter(mobGateBlock -> !mobGateBlock.broken).collect(Collectors.toList());
        return unbrokenBlocks.get(ThreadLocalRandom.current().nextInt(0, unbrokenBlocks.size())).location;
    }

    public void breakGateBlock(Location location) {
        if (isGateBlock(location) && !broken()) {
            MobGateBlock gateBlock = getGateBlock(location);
            if (gateBlock == null) return;
            gateBlock.location.getBlock().breakNaturally(null);
            gateBlock.broken = true;
        }
    }

    public Location repairGate(int blocks) {
        //for (int i = 0; i < blocks; i++) {
            if (repaired()) {
                return null;
            }
            List<MobGateBlock> brokenBlocks = gateBlocks.stream().filter(mobGateBlock -> mobGateBlock.broken).collect(Collectors.toList());
            MobGateBlock gateBlock = brokenBlocks.get(ThreadLocalRandom.current().nextInt(0, brokenBlocks.size()));
            Block block = gateBlock.location.getBlock();
            block.setType(gateBlock.material);
            block.setBlockData(gateBlock.blockData, true);
            gateBlock.broken = false;
            block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getPlaceSound(), 1, 1);
            return gateBlock.location;
        //}
    }

    public boolean repaired() {
        return gateBlocks.stream().noneMatch(mobGateBlock -> mobGateBlock.broken);
    }

    public boolean isInRepairZone(Location location) {
        return repairRegion.isInside(location);
    }

    public boolean isGateBlock(Location location) {
        return gateRegion.isInside(location);
    }

    public boolean isRepairedGateBlock(Location location) {
        if (gateRegion.isInside(location)) {
            MobGateBlock gateBlock = getGateBlock(location);
            if (gateBlock == null) return true;
            return !gateBlock.broken;
        }
        return true;
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

    public void debug() {
        HighlightUtil.highlight(repairRegion, ChatColor.GREEN);
        HighlightUtil.highlight(gateRegion, ChatColor.RED);
    }

    public void asyncDebug() {
        Hologram hologram = Holograms.createHologram(spawnLocation.clone().add(0, 1, 0));
        hologram.appendLine("§7" + mapSection);
    }

    private static class MobGateBlock {

        private final Location location;
        private final Material material;
        private final BlockData blockData;
        private boolean broken;

        private MobGateBlock(Location location, Material material, BlockData blockData) {
            this.location = location;
            this.material = material;
            this.blockData = blockData;
        }

    }

}
