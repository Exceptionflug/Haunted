package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import org.bukkit.Location;
import org.bukkit.entity.Ghast;

public class GhastMonster extends Monster {

    private Ghast ghast;

    @Override
    public void spawn(Location location) {
        ghast = location.getWorld().spawn(location, Ghast.class);
        ghast.setAI(false);
    }
}
