package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.monsters.goals.ThrowRangedAttackGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

public class FungusThrowerMonster extends ThrowerMonster {

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
        //ItemUtils.setSkullTexture(head, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWIyMDY0MzkwZTc5ZDllNTRjY2I0MThiMDczMzE1M2NmOTkyM2ZjNGE4ZDE0YWIxZDJiN2VmNTk2ODgzMWM5MyJ9fX0=");
        piglin.getEquipment().setHelmet(head);
        piglin.getEquipment().setItemInMainHand(new ItemStack(Material.WARPED_FUNGUS));
    }

    @Override
    public boolean canBreakGate() {
        return true;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float var4) {
        Snowball snowball = piglin.launchProjectile(Snowball.class);
        snowball.setItem(new ItemStack(Math.random() > 0.8 ? Material.CRIMSON_FUNGUS : Material.WARPED_FUNGUS));
    }
}
