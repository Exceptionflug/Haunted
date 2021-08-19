package de.exceptionflug.haunted.monster;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monsters.goals.AttackGateGoal;
import de.exceptionflug.projectvenom.game.GameContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public abstract class GateMonster implements Monster {

    private Entity entity;

    private boolean hasAttackGateGoal;
    private AttackGateGoal attackGateGoal;


    public void spawn(Entity entity, Location location) {
        this.entity = entity;
        if (((CraftEntity) entity).getHandle() instanceof Mob mob) {
            attackGateGoal = new AttackGateGoal((PathfinderMob) mob, 1.0D, 15);
            hasAttackGateGoal = true;
            mob.goalSelector.addGoal(1, attackGateGoal);
            mob.targetSelector.removeAllGoals();
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal(mob, net.minecraft.world.entity.player.Player.class, true));
        }
    }

    public void removeAttackGateGoal() {
        if (((CraftEntity) entity).getHandle() instanceof Mob mob) {
            mob.goalSelector.removeGoal(attackGateGoal);
            hasAttackGateGoal = false;
        }
    }
}
