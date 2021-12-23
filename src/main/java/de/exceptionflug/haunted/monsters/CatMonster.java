package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Cat;

public class CatMonster extends GateMonster {

    @Getter
    private Cat cat;

    @Override
    public void spawn(Location location) {
        cat = location.getWorld().spawn(location, Cat.class);
        super.spawn(cat, location);
        setAttackDamage(1);
        setMovementSpeed(0.4);
    }
}
