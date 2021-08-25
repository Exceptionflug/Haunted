package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.bukkit.Location;
import org.bukkit.entity.Skeleton;

public class SkeletonMonster extends GateMonster {

    private Skeleton skeleton;

    @Override
    public void spawn(Location location) {
        skeleton = location.getWorld().spawn(location, Skeleton.class);
        skeleton.setCanPickupItems(false);
        this.shouldAddMeleeAttackGoal = false;
        super.spawn(skeleton, location);
        if (getNmsEntity() instanceof AbstractSkeleton abstractSkeleton) {
            abstractSkeleton.reassessWeaponGoal();
        }
    }
}
