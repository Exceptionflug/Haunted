package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import org.bukkit.Location;
import org.bukkit.entity.Slime;

public class SlimeMonster extends Monster {

    private Slime slime;

    @Override
    public void spawn(Location location) {
        slime = location.getWorld().spawn(location, Slime.class);
    }

    public void setSize(int size) {
        slime.setSize(size);
    }
}
