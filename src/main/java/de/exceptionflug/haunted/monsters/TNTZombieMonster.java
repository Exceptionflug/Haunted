package de.exceptionflug.haunted.monsters;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monsters.goals.SelfExplodeGoal;
import de.exceptionflug.projectvenom.game.GameContext;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TNTZombieMonster extends ZombieMonster {

    @Inject
    public TNTZombieMonster(GameContext context) {
        super(context);
    }

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
