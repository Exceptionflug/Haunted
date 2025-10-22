package de.exceptionflug.haunted.npc;

import com.mojang.authlib.GameProfile;
import org.bukkit.Location;


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
        this.gameProfile = new GameProfile(uuid, gameProfile.getName());
        this.gameProfile.getProperties().putAll(gameProfile.getProperties());
    }

    public void spawn() {
        // TODO Use New 1.21.9 Feature
        // No Backwards compatibility?
        /*
        sendPacket(createPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        sendPacket(createSpawnPacket());
        sendPacket(createLayingPacket());
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(HauntedGameMode.class), () -> {
            sendPacket(createPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }, 10);
        */
    }

    public void despawn() {
        //sendPacket(createRemoveEntityPacket());
    }

    /*
    private void sendPacket(PacketContainer packetContainer) {
        Bukkit.getOnlinePlayers().forEach(player -> {
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

    private PacketContainer createLayingPacket() {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(entityId);
        WrappedDataWatcher.Serializer poseSerializer = WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass());
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, poseSerializer), EnumWrappers.EntityPose.SLEEPING.toNms());
        metadata.setMetadata(dataWatcher.getWatchableObjects());
        return metadata.getHandle();
    }
    */
}
