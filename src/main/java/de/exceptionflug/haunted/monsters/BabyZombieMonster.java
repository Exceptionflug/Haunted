package de.exceptionflug.haunted.monsters;

import org.bukkit.Location;

public class BabyZombieMonster extends ZombieMonster {
    @Override
    public void spawn(Location location) {
        super.spawn(location);
        getZombie().setBaby();
    }
}
