package de.exceptionflug.haunted.shop;

import de.exceptionflug.haunted.game.HauntedPlayer;
import org.bukkit.Location;

/**
 * Date: 17.09.2021
 *
 * @author Exceptionflug
 */
public interface Shop {

    void spawn();
    void despawn();

    Location triggerLocation();

    boolean interact(HauntedPlayer player);

}
