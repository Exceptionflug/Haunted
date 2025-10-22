package de.exceptionflug.haunted.game.gate;

import de.exceptionflug.haunted.util.CuboidRegion;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
            if (!hologram.isDisabled()) {
                return;
            }
            hologram.enable();
            if (hologramLocation2 != null) {
                hologram2.enable();
            }
            return;
        }
        List<String> hologramLines = new ArrayList<>();
        hologramLines.add(displayName);
        if (price != -1) {
            hologramLines.add("Preis: §b"+price+" Gold");
        }
        if (requiresPower) {
            hologramLines.add("§cBenötigt Strom");
        } else {
            hologramLines.add("§7(Rechtsklick auf Wand)");
        }
        hologram = DHAPI.createHologram("SectionGate_" + UUID.randomUUID(),
                hologramLocation, hologramLines);

        if (hologramLocation2 != null) {
            hologram2 = DHAPI.createHologram("SectionGate_" + UUID.randomUUID(),
                    hologramLocation2, hologramLines);
        }
    }

    public void despawn() {
        hologram.disable();
        if (hologram2 != null) {
            hologram2.disable();
        }
    }

    public void electricity() {
        if (requiresPower) {
            int line = 1;
            if (price != -1) {
                line = 2;
            }
            DHAPI.setHologramLine(hologram,  line, "§7(Rechtsklick auf Wand)");
            DHAPI.setHologramLine(hologram2,  line, "§7(Rechtsklick auf Wand)");
        }
    }

    public void unlock() {
        for (Location location : region.locations()) {
            Block block = location.getBlock();
            if (gateMaterials().contains(block.getType().name())) {
                block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation().add(0.5, 0, 0.5),
                        5, 0, 0, 0, 0.2, block.getBlockData());
                block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
                block.setType(Material.AIR);
            }
        }
        despawn();
    }

    public boolean isGateBlock(Location location) {
        Block block = location.getBlock();
        if (!gateMaterials.contains(block.getType().name())) {
            return false;
        }
        return region.isInside(location);
    }
}
