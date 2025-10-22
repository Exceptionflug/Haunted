package de.exceptionflug.haunted;

import de.exceptionflug.projectvenom.game.GameContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;

public class EntityUtils {

    private static final GameContext context = HauntedGameMode.getGameContext();

    public static Entity spawnCleanEntity(Location location, EntityType entityType) {
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        if (craftWorld != null) {
            net.minecraft.world.entity.Entity nmsEntity = craftWorld.createEntity(location, entityType.getEntityClass(), true);
            craftWorld.getHandle().addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return nmsEntity.getBukkitEntity();
        }
        return null;
    }

    public static void spawnPointsHologram(Location location, String text) {
        ServerLevel level =((CraftWorld) location.getWorld()).getHandle();
        ArmorStand nmsArmorStand = new ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND , level);
        nmsArmorStand.setMarker(true);
        nmsArmorStand.setInvisible(true);
        nmsArmorStand.setCustomName(CraftChatMessage.fromStringOrNull(text));
        nmsArmorStand.setCustomNameVisible(true);
        nmsArmorStand.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(nmsArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        CraftArmorStand armorStand = (CraftArmorStand) nmsArmorStand.getBukkitEntity();
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
