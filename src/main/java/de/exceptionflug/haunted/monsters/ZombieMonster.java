package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.monster.GateMonster;
import lombok.Getter;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class ZombieMonster extends GateMonster {

    @Getter
    private Zombie zombie;

    public boolean shouldAddZombieAttackGoal = true;
    private ZombieAttackGoal zombieAttackGoal;

    @Override
    public void spawn(Location location) {
        zombie = (Zombie) EntityUtils.spawnCleanEntity(location, EntityType.ZOMBIE);
        if (shouldAddZombieAttackGoal) this.shouldAddMeleeAttackGoal = false;
        super.spawn(zombie, location);
        if (shouldAddZombieAttackGoal && getNmsEntity() instanceof PathfinderMob mob) {
            zombieAttackGoal = new ZombieAttackGoal((net.minecraft.world.entity.monster.Zombie) mob, 1.0D, true);
            mob.goalSelector.addGoal(2, zombieAttackGoal);
        }
    }
}
