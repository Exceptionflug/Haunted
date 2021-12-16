package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.monsters.ThrowerMonster;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ThrowRangedAttackGoal extends Goal {

    private final Mob mob;
    private final ThrowerMonster monster;
    @Nullable
    private LivingEntity target;
    private int attackTime;
    private final double speedModifier;
    private int seeTime;
    private final int attackInterval;
    private final float attackRadius;
    private final float attackRadiusSqr;

    public ThrowRangedAttackGoal(Mob mob, ThrowerMonster monster) {
        this(mob, monster, 1.0D, 10, 10.0F);
    }

    public ThrowRangedAttackGoal(Mob mob, ThrowerMonster monster, double speedModifier, int attackInterval, float attackRadius) {
        this.attackTime = -1;
        this.mob = mob;
        this.monster = monster;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        LivingEntity var0 = this.mob.getTarget();
        if (var0 != null && var0.isAlive()) {
            this.target = var0;
            return true;
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return this.canUse() || !this.mob.getNavigation().isDone();
    }

    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        double distance = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);
        if (canSee) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(distance > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!canSee) {
                return;
            }

            float var3 = (float) Math.sqrt(distance) / this.attackRadius;
            float var4 = Mth.clamp(var3, 0.1F, 1.0F);
            monster.performRangedAttack(this.target, var4);
            this.attackTime = Mth.floor(var3 * attackInterval);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distance) / (double) this.attackRadius, attackInterval, attackInterval));
        }

    }
}
