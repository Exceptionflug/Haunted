package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monsters.goals.SelfExplodeGoal;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TNTZombieMonster extends ZombieMonster {

    @Override
    public void spawn(Location location) {
        shouldAddZombieAttackGoal = false;
        super.spawn(location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(2, new SelfExplodeGoal(this, 30));
        }
        getZombie().getEquipment().setHelmet(new ItemStack(Material.TNT));
    }
}
