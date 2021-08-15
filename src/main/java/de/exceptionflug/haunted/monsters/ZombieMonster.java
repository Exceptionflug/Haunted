package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class ZombieMonster implements Monster {

    private Zombie zombie;

    @Override
    public void spawn(Location location) {
        zombie = location.getWorld().spawn(location, Zombie.class);
    }

    @Override
    public void despawn() {
        zombie.remove();
    }

    @Override
    public boolean alive() {
        return !zombie.isDead();
    }

}
