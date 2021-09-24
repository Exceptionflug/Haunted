package de.exceptionflug.haunted.game.gate;

import de.exceptionflug.haunted.util.CuboidRegion;
import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import de.exceptionflug.mccommons.holograms.line.TextHologramLine;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.Set;

/**
 * Date: 17.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public final class SectionGate {

    private final CuboidRegion region;
    private final Set<String> gateMaterials;
    private final Location hologramLocation;
    private final Location hologramLocation2;
    private final String displayName;
    private final int price;
    private final boolean requiresPower;
    private Hologram hologram;
    private Hologram hologram2;
    private TextHologramLine powerInteractLine;
    private TextHologramLine powerInteractLine2;

    public SectionGate(CuboidRegion region, Set<String> gateMaterials, Location hologramLocation, Location hologramLocation2, String displayName, int price, boolean requiresPower) {
        this.region = region;
        this.gateMaterials = gateMaterials;
        this.hologramLocation = hologramLocation;
        this.hologramLocation2 = hologramLocation2;
        this.displayName = displayName;
        this.price = price;
        this.requiresPower = requiresPower;
    }

    public void spawnHologram() {
        if (hologram != null) {
            if (!hologram.isDespawned()) {
                return;
            }
            hologram.spawn();
            if (hologramLocation2 != null) {
                hologram2.spawn();
            }
            return;
        }
        hologram = Holograms.createHologram(hologramLocation);
        hologram.appendLine(displayName);
        if (price != -1) {
            hologram.appendLine("Preis: §b"+price+" Gold");
        }
        if (requiresPower) {
            powerInteractLine = hologram.appendLine("§cBenötigt Strom");
        } else {
            powerInteractLine = hologram.appendLine("§7(Rechtsklick auf Wand)");
        }
        hologram.spawn();

        if (hologramLocation2 != null) {
            hologram2 = Holograms.createHologram(hologramLocation2);
            hologram2.appendLine(displayName);
            if (price != -1) {
                hologram2.appendLine("Preis: §b"+price+" Gold");
            }
            if (requiresPower) {
                powerInteractLine2 = hologram2.appendLine("§cBenötigt Strom");
            } else {
                powerInteractLine2 = hologram2.appendLine("§7(Rechtsklick auf Wand)");
            }
            hologram2.spawn();
        }
    }

    public void despawn() {
        hologram.despawn();
        if (hologram2 != null) {
            hologram2.despawn();
        }
    }

    public void electricity() {
        if (requiresPower) {
            powerInteractLine.setText("§7(Rechtsklick auf Wand)");
            if (powerInteractLine2 != null) {
                powerInteractLine2.setText("§7(Rechtsklick auf Wand)");
            }
        }
    }

    public void unlock() {
        for (Location location : region.locations()) {
            Block block = location.getBlock();
            if (gateMaterials().contains(block.getType().name())) {
                block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0, 0.5),
                        5, 0, 0, 0, 0.2, block.getBlockData());
                block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
                block.setType(Material.AIR);
            }
        }
        hologram.despawn();
        if (hologram2 != null) {
            hologram2.despawn();
        }
    }

    public boolean isGateBlock(Location location) {
        Block block = location.getBlock();
        if (!gateMaterials.contains(block.getType().name())) {
            return false;
        }
        return region.isInside(location);
    }
}
