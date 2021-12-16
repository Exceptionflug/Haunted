package de.exceptionflug.haunted.monsters;

import org.bukkit.Location;
import org.bukkit.entity.Cow;

public class CowMonster extends AnimalMonster {
    private Cow cow;

    @Override
    public void spawn(Location location) {
        cow = location.getWorld().spawn(location, Cow.class);
        super.spawn(cow, location);
    }
}
