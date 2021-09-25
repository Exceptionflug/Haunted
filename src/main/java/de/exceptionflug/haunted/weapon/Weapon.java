package de.exceptionflug.haunted.weapon;

import de.exceptionflug.haunted.game.HauntedPlayer;
import org.bukkit.entity.Player;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
public interface Weapon {

    void give(HauntedPlayer player, int slot);

    void destroy();

}
