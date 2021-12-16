package de.exceptionflug.haunted.monster;

import de.exceptionflug.haunted.monsters.goals.BreakGateGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

public abstract class GateMonster extends Monster {

    public boolean shouldAddMeleeAttackGoal = true;
    private BreakGateGoal attackGateGoal;

    @Override
    public boolean canBreakGate() {
        return true;
    }

    public void spawn(LivingEntity entity, Location location) {
        super.spawn(entity, location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            attackGateGoal = new BreakGateGoal(mob);
            mob.goalSelector.removeAllGoals();
            //mob.goalSelector.addGoal(1, attackGateGoal);
            if (shouldAddMeleeAttackGoal) mob.goalSelector.addGoal(4, new MeleeAttackGoal(mob, 1.0D, true));
            mob.goalSelector.addGoal(6, new LookAtPlayerGoal(mob, Player.class, 8.0F));
            mob.goalSelector.addGoal(6, new RandomLookAroundGoal(mob));
            mob.targetSelector.removeAllGoals();
            mob.targetSelector.addGoal(1, getPlayerGoal());
        }
    }

    public void removeAttackGateGoal() {
        if (((CraftEntity) getEntity()).getHandle() instanceof Mob mob) {
            mob.goalSelector.removeGoal(attackGateGoal);
            attackGateGoal = null;
        }
    }
}
