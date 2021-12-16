package de.exceptionflug.haunted.monsters;

import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;

public class WolfMonster extends AnimalMonster {
    private Wolf wolf;

    @Override
    public void spawn(Location location) {
        wolf = location.getWorld().spawn(location, Wolf.class);
        super.spawn(wolf, location);
        if (getNmsEntity() instanceof Animal mob) {
            mob.goalSelector.addGoal(4, new LeapAtTargetGoal(mob, 0.4F));
        }
    }
}
