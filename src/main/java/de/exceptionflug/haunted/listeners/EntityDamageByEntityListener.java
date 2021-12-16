package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
@Component
public final class EntityDamageByEntityListener implements Listener {

    private final GameContext gameContext;

    @Inject
    public EntityDamageByEntityListener(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(true);
            return;
        }
        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() != null && projectile.getShooter() instanceof Player player) {
                HauntedPlayer shooter = gameContext.player(player);
                if (shooter.spectator()) {
                    event.setCancelled(true);
                    return;
                }
                if (projectile.getPersistentDataContainer().has(Gun.SHOOTER_KEY, PersistentDataType.STRING)) {
                    if (event.getEntity() instanceof Player) {
                        event.setCancelled(true);
                        return; // No friendly fire
                    }
                    HauntedPlayer hauntedPlayer = gameContext.player(UUID.fromString(projectile.getPersistentDataContainer().get(Gun.SHOOTER_KEY, PersistentDataType.STRING)));
                    if (hauntedPlayer != null) {
                        shooter = hauntedPlayer;
                    }
                }
                if (projectile.getPersistentDataContainer().has(Gun.DAMAGE_KEY, PersistentDataType.DOUBLE)) {
                    event.setDamage(EntityDamageEvent.DamageModifier.BASE, projectile.getPersistentDataContainer().get(Gun.DAMAGE_KEY, PersistentDataType.DOUBLE));
                }
                if (projectile.getPersistentDataContainer().has(Gun.HEADSHOT_KEY, PersistentDataType.BYTE)) {
                    shooter.giveGold(10);
                    EntityUtils.spawnPointsHologram(event.getEntity().getLocation(), "§6+10");
                    shooter.playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.getDamage() * 1.5D);
                } else {
                    shooter.giveGold(5);
                    EntityUtils.spawnPointsHologram(event.getEntity().getLocation(), "§6+5");
                }
                Entity entity = event.getEntity();
                if (entity.isInvulnerable()) {
                    if (entity.getPassengers().size() > 0) {
                        entity.getPassengers().forEach(e -> {
                            if (!e.isInvulnerable() && e instanceof LivingEntity le) le.damage(event.getDamage(), event.getDamager());
                        });
                    }
                    if (entity.getVehicle() != null) {
                        Entity e = entity.getVehicle();
                        if (!e.isInvulnerable() && e instanceof LivingEntity le) le.damage(event.getDamage(), event.getDamager());
                    }
                }
            }
        }
    }

}
