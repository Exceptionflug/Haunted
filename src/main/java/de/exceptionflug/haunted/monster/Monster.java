package de.exceptionflug.haunted.monster;

import org.bukkit.Location;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public interface Monster {

    void spawn(Location location);
    void despawn();
    boolean alive();

}
