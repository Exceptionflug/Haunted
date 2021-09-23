package de.exceptionflug.haunted.util;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Date: 23.09.2021
 *
 * @author Exceptionflug
 */
public class ShulkerUtil {

    public static Shulker spawnShulker(Location loc) {
        Shulker shulker = (Shulker) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(),
                loc.getY(), loc.getZ()), EntityType.SHULKER);
        shulker.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
        shulker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        shulker.setAI(false);
        shulker.setSilent(true);
        shulker.setGravity(false);
        shulker.setInvulnerable(true);
        return shulker;
    }

}
