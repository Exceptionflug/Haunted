package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.monster.GateMonster;
import de.exceptionflug.haunted.monsters.goals.ThrowRangedAttackGoal;
import de.exceptionflug.haunted.util.ItemUtils;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class FungusThrowerMonster extends GateMonster implements RangedMonster {

    private Piglin piglin;

    @Override
    public void spawn(Location location) {
        piglin = (Piglin) EntityUtils.spawnCleanEntity(location, EntityType.PIGLIN);
        super.spawn(piglin, location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(2, new ThrowRangedAttackGoal(mob, this));
        }
        piglin.setImmuneToZombification(true);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemUtils.setSkullTexture(head, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWIyMDY0MzkwZTc5ZDllNTRjY2I0MThiMDczMzE1M2NmOTkyM2ZjNGE4ZDE0YWIxZDJiN2VmNTk2ODgzMWM5MyJ9fX0=");
        piglin.getEquipment().setHelmet(head);
        piglin.getEquipment().setItemInMainHand(new ItemStack(Material.WARPED_FUNGUS));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float var4) {
        Snowball snowball = piglin.launchProjectile(Snowball.class, piglin.getLocation().getDirection().multiply(0.45));
        snowball.setItem(new ItemStack(Math.random() > 0.8 ? Material.CRIMSON_FUNGUS : Material.WARPED_FUNGUS));
    }

    @Override
    public void performProjectileHit(Projectile projectile, LivingEntity target) {
        Location location = projectile.getLocation();
        Snowball snowball = (Snowball) projectile;
        AreaEffectCloud cloud = (AreaEffectCloud) location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        cloud.clearCustomEffects();
        cloud.setRadius(2);
        cloud.setDuration(30);
        cloud.setSilent(true);
        boolean crimson = snowball.getItem().getType() == Material.CRIMSON_FUNGUS;
        cloud.setParticle(crimson ? Particle.CRIMSON_SPORE : Particle.WARPED_SPORE);
        cloud.setBasePotionData(new PotionData(PotionType.HARMING));
    }

    @Override
    public double getProjectileDamage(LivingEntity target) {
        return .5;
    }
}
