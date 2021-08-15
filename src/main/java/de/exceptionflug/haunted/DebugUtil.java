package de.exceptionflug.haunted;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
public class DebugUtil {

    public static void broadcastMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

}
