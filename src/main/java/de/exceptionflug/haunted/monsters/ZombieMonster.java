package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class ZombieMonster extends GateMonster {

    private Zombie zombie;

    @Override
    public void spawn(Location location) {
        zombie = location.getWorld().spawn(location, Zombie.class);
        //zombie.setShouldBurnInDay(false);
        zombie.setCanPickupItems(false);
        super.spawn(zombie, location);
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
