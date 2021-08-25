package de.exceptionflug.haunted.monster;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public abstract class Monster {

    @Getter
    private LivingEntity entity;

    public abstract void spawn(Location location);

    public void spawn(LivingEntity entity, Location location) {
        this.entity = entity;
    }

    public void despawn() {
        this.entity.remove();
    }

    public boolean alive() {
        return !entity.isDead();
    }

    public net.minecraft.world.entity.Entity getNmsEntity() {
        return ((CraftEntity) entity).getHandle();
    }

    public void spawnParticle(Particle particle, int count) {
        Location location = getEntity().getLocation();
        getEntity().getWorld().spawnParticle(particle, location, count);
    }
}
