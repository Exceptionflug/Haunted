package de.exceptionflug.haunted.monsters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public interface RangedMonster {
    void performRangedAttack(LivingEntity target, float var4);
    void performProjectileHit(Projectile projectile, LivingEntity target);
    double getProjectileDamage(LivingEntity target);
}
