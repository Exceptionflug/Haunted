package de.exceptionflug.haunted.weapon;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;

/**
 * Date: 13.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public enum GunType {

    SHOTGUN("Schrotflinte", 500, 1, 0, 2, 5, 5, 500, 0.5, 40, 0.05, 4, Egg.class, Material.STONE_HOE, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, Sound.BLOCK_PISTON_EXTEND, 2),
    PISTOL("Pistole", 500, 1, 0, 4, 1, 16, 300, 0.1, 30, 0.02, 6, Snowball.class, Material.STICK, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2, Sound.BLOCK_PISTON_EXTEND, 2),
    P90("P90", 500, 3, 100, 4, 1, 18, 0, 0.1, 40, 0.02, 6, Snowball.class, Material.BLAZE_ROD, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2, Sound.BLOCK_PISTON_EXTEND, 2),
    POSEIDONS_REVENGE("§5Poseidon's Rache", 300, 1, 0, 3, 3, 12, 0, 0.1, 60, 0.05, 8, Trident.class, Material.GOLDEN_SHOVEL, Sound.ITEM_TRIDENT_THROW, 2, Sound.ITEM_TRIDENT_RETURN, 2),
    ROCKET_LAUNCHER("Raketenwerfer", 300, 1, 0, 2, 1, 5, 1000, 0.5, 60, 0.05, 12, WitherSkull.class, Material.GOLDEN_HOE, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2, Sound.BLOCK_PISTON_EXTEND, 1),
    POTION_THROWER("§5Trankschleuder", 300, 1, 0, 1, 3, 5, 1000, 0.1, 60, 0.05, 12, LingeringPotion.class, Material.CROSSBOW, Sound.ENTITY_LINGERING_POTION_THROW, 2, Sound.ENTITY_IRON_GOLEM_REPAIR, 2),
    SNIPER("Sniper", 100, 1, 0, 5, 1, 1, 1000, 0.7, 20, 0, 60, LlamaSpit.class, Material.NETHERITE_HOE, Sound.ENTITY_GENERIC_EXPLODE, 2, Sound.BLOCK_PISTON_EXTEND, 0),
    SNOWBALL_CANON("Schneeballkanone", 300, 1, 0, 2, 6, 1, 1000, 0.5, 20, 0.05, 6, Snowball.class, Material.IRON_SHOVEL, Sound.ENTITY_SNOWBALL_THROW, 2, Sound.BLOCK_SNOW_BREAK, 0);

    private final String displayName;
    private final int maxAmmunition;
    private final int volley;
    private final int volleyDelay;
    private final double speed;
    private final int bulletsPerShot;
    private final int rounds;
    private final int fireDelay;
    private final double recoil;
    private final int reloadDelay;
    private final double spread;
    private final double damage;
    private final Class<? extends Projectile> projectileType;
    private final Material itemType;
    private final Sound shootSound;
    private final float shootSoundPitch;
    private final Sound reloadSound;
    private final float reloadSoundPitch;

    GunType(String displayName, int maxAmmunition, int volley, int volleyDelay, double speed, int bulletsPerShot, int rounds, int fireDelay, double recoil, int reloadDelay, double spread, double damage, Class<? extends Projectile> projectileType, Material itemType, Sound shootSound, float shootSoundPitch, Sound reloadSound, float reloadSoundPitch) {
        this.displayName = displayName;
        this.maxAmmunition = maxAmmunition;
        this.volley = volley;
        this.volleyDelay = volleyDelay;
        this.speed = speed;
        this.bulletsPerShot = bulletsPerShot;
        this.rounds = rounds;
        this.fireDelay = fireDelay;
        this.recoil = recoil;
        this.reloadDelay = reloadDelay;
        this.spread = spread;
        this.damage = damage;
        this.projectileType = projectileType;
        this.itemType = itemType;
        this.shootSound = shootSound;
        this.shootSoundPitch = shootSoundPitch;
        this.reloadSound = reloadSound;
        this.reloadSoundPitch = reloadSoundPitch;
    }

}
