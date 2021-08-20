package de.exceptionflug.haunted;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.projectvenom.game.GameContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class GateUtils {


    private static final GameContext context = HauntedGameMode.getGameContext();

    private static MobGate getMobGate(Location location) {
        return context.<HauntedMap>currentMap().mobGateByGateBlock(location);
    }

    private static Location blockPosToLocation(BlockPos blockPos) {
        return new Location(null, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static boolean isGateBlock(Location location) {
        return getMobGate(location) != null;
    }

    public static boolean isGateBlock(BlockPos blockPos) {
        return isGateBlock(blockPosToLocation(blockPos));
    }

    public static boolean isGateBlock(int x, int y, int z) {
        return isGateBlock(new Location(null, x, y, z));
    }

    private static final Map<Location, Boolean> locks = new HashMap<>();

    public static boolean isGateBlockLocked(int x, int y, int z) {
        return locks.containsKey(new Location(null, x, y, z));
    }

    public static void lockGateBlock(int x, int y, int z) {
        locks.put(new Location(null, x, y, z), true);
    }

    public static void unlockGateBlock(int x, int y, int z) {
        locks.remove(new Location(null, x, y, z));
    }

    public static boolean isGateBlockLocked(BlockPos blockPos) {
        return isGateBlockLocked(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static boolean isRepairedGateBlock(Location location) {
        MobGate mobGate = getMobGate(location);
        return mobGate != null && mobGate.isRepairedGateBlock(location);
    }

    public static boolean isRepairedGateBlock(BlockPos blockPos) {
        return isRepairedGateBlock(blockPosToLocation(blockPos));
    }

    public static void breakGateBlock(BlockPos blockPos) {
        Location location = blockPosToLocation(blockPos);
        getMobGate(location).breakGateBlock(location);
    }
}
