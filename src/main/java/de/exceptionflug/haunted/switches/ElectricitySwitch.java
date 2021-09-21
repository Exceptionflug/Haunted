package de.exceptionflug.haunted.switches;

import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Location;

/**
 * Date: 21.09.2021
 *
 * @author Exceptionflug
 */
@Data
@Accessors(fluent = true)
public class ElectricitySwitch {

    private final Location location;
    private final Location leverLocation;
    private final String name;
    private final int price;
    private boolean pulled;

    private Hologram hologram;

    public void spawn() {
        hologram = Holograms.createHologram(location);
        hologram.appendLine(name);
        hologram.appendLine("§7Preis: §b" + price + " Gold");
        hologram.appendLine("§7(Rechtklick auf Hebel)");
    }


}
