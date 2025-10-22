package de.exceptionflug.haunted.switches;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

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
        hologram = DHAPI.createHologram("ElectricitySwitch_" + UUID.randomUUID(),
                location,
                List.of(name, "§7Preis: §b" + price + " Gold", "§7(Rechtklick auf Hebel)"));
    }


}
