package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;

public class SkeletonMonster extends GateMonster implements RangedMonster {

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

    @Override
    public void performRangedAttack(LivingEntity target, float var4) {

    }

    @Override
    public void performProjectileHit(Projectile projectile, LivingEntity target) {
        spawnParticle(Particle.HAPPY_VILLAGER, 2);
    }

    @Override
    public double getProjectileDamage(LivingEntity target) {
        return 1;
    }
}
