package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.Animal;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class AnimalMonster extends Monster {

    @Override
    public boolean canBreakGate() {
        return true;
    }

    public void spawn(LivingEntity entity, Location location) {
        super.spawn(entity, location);
        if (getNmsEntity() instanceof Animal mob) {
            mob.goalSelector.removeAllGoals(goal -> true);
            mob.goalSelector.addGoal(1, new FloatGoal(mob));
            mob.goalSelector.addGoal(5, new MeleeAttackGoal(mob, 1.0D, true));
            mob.targetSelector.removeAllGoals(goal -> true);
            mob.targetSelector.addGoal(1, getPlayerGoal());
        }
    }
}
