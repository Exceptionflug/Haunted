package de.exceptionflug.haunted.monsters;

import lombok.Getter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;

public class FarmerZombieMonster extends ZombieMonster {

    @Getter
    private ZombieVillager zombie;

    private ZombieAttackGoal zombieAttackGoal;

    @Override
    public void spawn(Location location) {
        zombie = location.getWorld().spawn(location, ZombieVillager.class);
        super.spawn(zombie, location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            zombieAttackGoal = new ZombieAttackGoal((net.minecraft.world.entity.monster.Zombie) mob, 1.1D, true);
            mob.goalSelector.addGoal(2, zombieAttackGoal);
        }
        getZombie().setVillagerProfession(Villager.Profession.FARMER);
        getZombie().getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_HOE));
    }
}

