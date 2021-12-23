package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

public class LibrarianZombieMonster extends Monster {

    public ZombieVillager zombie;

    @Override
    public void spawn(Location location) {
        zombie = location.getWorld().spawn(location, ZombieVillager.class);
        spawn(zombie, location);
        zombie.setVillagerProfession(Villager.Profession.LIBRARIAN);
    }
}
