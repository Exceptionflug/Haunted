package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monsters.RangedMonster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Date: 13.08.2021
 *
 * @author Exceptionflug
 */
@Singleton
public final class ProjectileHitListener implements Listener {

    private final GameContext context;

    @Inject
    public ProjectileHitListener(GameContext context) {
        this.context = context;
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (event.getHitBlock() != null) {
            if (event.getHitBlock().getType() == Material.BARRIER) return;
            spawnParticles(event.getHitBlock(), projectile);
        } else if (event.getHitEntity() != null) {
            if (event.getHitEntity() instanceof Mob mob) {
                double distanceY = Math.abs(mob.getEyeLocation().getY() - projectile.getLocation().getY());
                if (distanceY <= 0.5) {
                    projectile.getPersistentDataContainer().set(Gun.HEADSHOT_KEY, PersistentDataType.BYTE, (byte) 1);
                }
            }
            spawnParticles(event.getHitEntity(), projectile);
        }
        if (projectile.getShooter() != null && context.phase().ingamePhase() && context.<HauntedIngamePhase>phase().wave().monsterByEntity((Entity) projectile.getShooter()) instanceof RangedMonster thrower && event.getHitEntity() instanceof LivingEntity livingEntity) {
            thrower.performProjectileHit(projectile, livingEntity);
            double damage = thrower.getProjectileDamage(livingEntity);
            if (damage > 0) livingEntity.damage(damage);
        }
    }

    private void spawnParticles(Block hitBlock, Projectile projectile) {
        projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(),
                10, 0, 0, 0, 0.2, hitBlock.getBlockData());
        projectile.getWorld().playSound(hitBlock.getLocation(), hitBlock.getBlockData().getSoundGroup().getHitSound(), 1, 1);
        if (projectile instanceof AbstractArrow) {
            projectile.remove();
        }
    }

    private void spawnParticles(Entity hitEntity, Projectile projectile) {
        projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(),
                10, 0, 0, 0, 0.2, Material.REDSTONE_BLOCK.createBlockData());
        projectile.getWorld().playSound(projectile.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
    }


}
