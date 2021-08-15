package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.DebugUtil;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Date: 13.08.2021
 *
 * @author Exceptionflug
 */
@Component
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
    }

    private void spawnParticles(Block hitBlock, Projectile projectile) {
        projectile.getWorld().spawnParticle(Particle.BLOCK_CRACK, projectile.getLocation(),
                10, 0, 0, 0, 0.2, hitBlock.getBlockData());
        projectile.getWorld().playSound(hitBlock.getLocation(), hitBlock.getBlockData().getSoundGroup().getHitSound(), 1, 1);
        if (projectile instanceof AbstractArrow) {
            projectile.remove();
        }
    }

    private void spawnParticles(Entity hitEntity, Projectile projectile) {
        projectile.getWorld().spawnParticle(Particle.BLOCK_CRACK, projectile.getLocation(),
                10, 0, 0, 0, 0.2, Material.REDSTONE_BLOCK.createBlockData());
        projectile.getWorld().playSound(projectile.getLocation(), Sound.BLOCK_STONE_PLACE, 1, 1);
    }


}
