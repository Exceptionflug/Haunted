package de.exceptionflug.haunted.section;

import de.exceptionflug.haunted.util.CuboidRegion;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Location;

import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class MapSection {

    private final String name;
    private final String displayName;
    private final List<CuboidRegion> regions;

    public MapSection(String name, String displayName, List<CuboidRegion> regions) {
        this.name = name;
        this.displayName = displayName;
        this.regions = regions;
    }

    public boolean isInside(Location location) {
        for (CuboidRegion region : regions) {
            if (region.isInside(location)) {
                return true;
            }
        }
        return false;
    }

}
