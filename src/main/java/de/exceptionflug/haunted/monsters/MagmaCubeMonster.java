package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import org.bukkit.Location;
import org.bukkit.entity.MagmaCube;

public class MagmaCubeMonster extends Monster {

    private MagmaCube cube;

    @Override
    public void spawn(Location location) {
        cube = location.getWorld().spawn(location, MagmaCube.class);
    }

    public void setSize(int size) {
        cube.setSize(size);
    }

}
