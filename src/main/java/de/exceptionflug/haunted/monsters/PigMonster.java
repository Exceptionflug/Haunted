package de.exceptionflug.haunted.monsters;

import org.bukkit.Location;
import org.bukkit.entity.Pig;

public class PigMonster extends AnimalMonster {
    private Pig pig;

    @Override
    public void spawn(Location location) {
        pig = location.getWorld().spawn(location, Pig.class);
        super.spawn(pig, location);
        setAttackDamage(1.5);
        setMovementSpeed(.3);
    }
}
