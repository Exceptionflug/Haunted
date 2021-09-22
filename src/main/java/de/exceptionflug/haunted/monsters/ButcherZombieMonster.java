package de.exceptionflug.haunted.monsters;

import com.google.inject.Inject;
import de.exceptionflug.projectvenom.game.GameContext;
import lombok.Getter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;

public class ButcherZombieMonster extends ZombieMonster {

    @Getter
    private ZombieVillager zombie;

    private ZombieAttackGoal zombieAttackGoal;

    @Inject
    public ButcherZombieMonster(GameContext context) {
        super(context);
    }

    @Override
    public void spawn(Location location) {
        zombie = location.getWorld().spawn(location, ZombieVillager.class);
        super.spawn(zombie, location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            zombieAttackGoal = new ZombieAttackGoal((net.minecraft.world.entity.monster.Zombie) mob, 1.5D, true);
            mob.goalSelector.addGoal(2, zombieAttackGoal);
        }
        getZombie().setVillagerProfession(Villager.Profession.BUTCHER);
        getZombie().getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
    }
}
