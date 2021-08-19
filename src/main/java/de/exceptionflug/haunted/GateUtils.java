package de.exceptionflug.haunted;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.projectvenom.game.GameContext;
import net.minecraft.core.BlockPos;
import org.bukkit.Location;

public class GateUtils {


    private static final GameContext context = HauntedGameMode.getGameContext();

    public static boolean isGateBlock(Location location) {
        return context.<HauntedMap>currentMap().mobGateByGateBlock(location) != null;
    }

    public static boolean isGateBlock(BlockPos blockPos) {
        return isGateBlock(new Location(null, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public static boolean isGateBlock(int x, int y, int z) {
        return context.<HauntedMap>currentMap().mobGateByGateBlock(new Location(null, x, y, z)) != null;
    }

    public static boolean isGateBlockLocked(int x, int y, int z) {
        return false;
    }

    public static void lockGateBlock(int x, int y, int z) {

    }
}
