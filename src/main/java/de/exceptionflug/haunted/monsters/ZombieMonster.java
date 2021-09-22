package de.exceptionflug.haunted.monsters;

import com.google.inject.Inject;
import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.monster.GateMonster;
import de.exceptionflug.projectvenom.game.GameContext;
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

    @Inject
    public ZombieMonster(GameContext gameContext) {
        super(gameContext);
    }

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
