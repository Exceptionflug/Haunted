package de.exceptionflug.haunted.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.mojang.authlib.GameProfile;
import de.exceptionflug.haunted.HauntedGameMode;
import de.exceptionflug.mccommons.core.packetwrapper.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 17.08.2021
 *
 * @author Exceptionflug
 */
public class NPC {

    private final Location location;
    private final GameProfile gameProfile;
    private final UUID uuid = UUID.randomUUID();
    private final int entityId = ThreadLocalRandom.current().nextInt(20000, 30000);

    public NPC(Location location, GameProfile gameProfile) {
        this.location = location;
        this.gameProfile = gameProfile;
    }

    public void spawn() {
        sendPacket(createPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        sendPacket(createSpawnPacket());
        sendPacket(createBedDeclarePacket());
        sendPacket(createLayingPacket());
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(HauntedGameMode.class), () -> {
            sendPacket(createPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }, 10);
    }

    public void despawn() {
        sendPacket(createRemoveEntityPacket());
    }

    private void sendPacket(PacketContainer packetContainer) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (gameProfile.getId().equals(player.getUniqueId()) && packetContainer.getType() == PacketType.Play.Server.PLAYER_INFO) {
                return;
            }
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (Exception exception) {
            }
        });
    }

    private PacketContainer createRemoveEntityPacket() {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(Collections.singletonList(entityId));
        return destroy.getHandle();
    }

    private PacketContainer createPlayerInfoPacket(EnumWrappers.PlayerInfoAction action) {
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(action);

        PlayerInfoData data = new PlayerInfoData(WrappedGameProfile.fromHandle(gameProfile),
                10, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromLegacyText(""));
        playerInfo.setData(Collections.singletonList(data));
        return playerInfo.getHandle();
    }

    private PacketContainer createSpawnPacket() {
        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        spawn.setEntityID(entityId);
        spawn.setX(location.getX());
        spawn.setY(location.getY());
        spawn.setZ(location.getZ());
        spawn.setYaw(location.getYaw());
        spawn.setPitch(location.getPitch());
        spawn.setPlayerUUID(gameProfile.getId());
        return spawn.getHandle();
    }

    private PacketContainer createBedDeclarePacket() {
        Location location = this.location.getBlock().getRelative(BlockFace.DOWN).getLocation();

        WrapperPlayServerTileEntityData tileEntityData = new WrapperPlayServerTileEntityData();
        tileEntityData.setAction(11);
        tileEntityData.setLocation(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        tileEntityData.setNbtData(NbtFactory.ofCompound("test"));
        return tileEntityData.getHandle();
    }

    private PacketContainer createLayingPacket() {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(entityId);
        WrappedDataWatcher.Serializer poseSerializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass());
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, poseSerializer), EnumWrappers.EntityPose.SLEEPING.toNms());
        metadata.setMetadata(dataWatcher.getWatchableObjects());
        return metadata.getHandle();
    }
}
