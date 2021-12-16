package de.exceptionflug.haunted;

import de.exceptionflug.projectvenom.game.GameContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;

public class EntityUtils {

    private static final GameContext context = HauntedGameMode.getGameContext();

    public static Entity spawnCleanEntity(Location location, EntityType entityType) {
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld != null) {
            net.minecraft.world.entity.Entity nmsEntity = craftWorld.createEntity(location, entityType.getEntityClass());
            craftWorld.getHandle().addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return nmsEntity.getBukkitEntity();
        }
        return null;
    }

    public static void spawnPointsHologram(Location location, String text) {
        ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0, 0.5, 0), ArmorStand.class);
        armorStand.setMarker(true);
        armorStand.setInvisible(true);
        armorStand.setCustomName(text);
        armorStand.setCustomNameVisible(true);
        new Runnable() {
            private final BukkitTask task = Bukkit.getScheduler().runTaskTimer(context.plugin(), this, 0, 1);
            private int ticks = 0;

            @Override
            public void run() {
                ticks++;
                armorStand.teleport(armorStand.getLocation().add(0, 0.05, 0));
                if (ticks > 10) {
                    armorStand.remove();
                    task.cancel();
                }
            }
        };
    }
}
