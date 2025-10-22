package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.ItemModifier;
import de.exceptionflug.haunted.monsters.goals.ThrowRangedAttackGoal;
import de.exceptionflug.haunted.util.ItemUtils;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlimeThrowerZombieMonster extends ZombieMonster implements RangedMonster {

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(2, new ThrowRangedAttackGoal(mob, this, 1.0D, 40, 10.0F));
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemUtils.setSkullTexture(head, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk1YWVlYzZiODQyYWRhODY2OWY4NDZkNjViYzQ5NzYyNTk3ODI0YWI5NDRmMjJmNDViZjNiYmI5NDFhYmU2YyJ9fX0=");
        getZombie().getEquipment().setHelmet(head);
        getZombie().getEquipment().setChestplate(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.LIME));
        getZombie().getEquipment().setLeggings(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_LEGGINGS), Color.LIME));
        getZombie().getEquipment().setBoots(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_BOOTS), Color.LIME));
        getZombie().getEquipment().setItemInMainHand(new ItemStack(Material.SLIME_BALL));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float var4) {
        Snowball snowball = getZombie().launchProjectile(Snowball.class, getZombie().getLocation().getDirection().multiply(0.75));
        snowball.setShooter(getZombie());
        snowball.setItem(new ItemStack(Material.SLIME_BALL));
    }

    @Override
    public void performProjectileHit(Projectile projectile, LivingEntity target) {
        if (target instanceof Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 0));
        }
    }

    @Override
    public double getProjectileDamage(LivingEntity target) {
        return 0;
    }
}
