package de.exceptionflug.haunted;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityUtils {

    public static Entity spawnCleanEntity(Location location, EntityType entityType) {
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld != null) {
            net.minecraft.world.entity.Entity nmsEntity = craftWorld.createEntity(location, entityType.getEntityClass());
            craftWorld.getHandle().addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return nmsEntity.getBukkitEntity();
        }
        return null;
    }
}
