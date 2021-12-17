package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.monster.Monster;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class SelfExplodeGoal extends Goal {

    private final Monster monster;
    private final Mob mob;
    private final int maxFuseTime;
    private int fuseTime = -1;
    private LivingEntity target;

    public SelfExplodeGoal(Monster monster, int fuseTime) {
        this.monster = monster;
        this.mob = (Mob) monster.getNmsEntity();
        this.maxFuseTime = fuseTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public void start() {
        System.out.println("start self explode SB " + mob);
        this.mob.getNavigation().stop();
        this.target = this.mob.getTarget();
        System.out.println("start self explode EB " + mob);
    }

    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return this.fuseTime > 0 || target != null && this.mob.distanceToSqr(target) < 9.0D;
    }

    public void tick() {
        System.out.println("TICK");
        if (this.target == null) {
            this.fuseTime = -1;
        } else if (this.mob.distanceToSqr(this.target) > 49.0D) {
            this.fuseTime = -1;
        } else if (!this.mob.getSensing().hasLineOfSight(this.target)) {
            this.fuseTime = -1;
        } else {
            this.fuseTime++;
            if (this.fuseTime < 0) {
                this.fuseTime = 0;
                this.mob.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
            }

            if (this.fuseTime >= 0 && this.fuseTime % 5 == 0) {
                if (this.fuseTime % 10 == 0) {
                    this.mob.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.REDSTONE_BLOCK)));
                } else {
                    this.mob.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.TNT)));
                    this.monster.getEntity().getWorld().spawnParticle(Particle.LAVA, this.monster.getEntity().getEyeLocation(), 3);
                }
            }

            if (this.fuseTime >= this.maxFuseTime) {
                this.fuseTime = this.maxFuseTime;
                this.explode();
            }
        }
    }

    private void explode() {
        System.out.println("EXPLODE");
        this.mob.die(DamageSource.explosion((LivingEntity) null));
        this.mob.discard();
        this.mob.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
        //this.mob.level.addParticle(ParticleTypes.EXPLOSION, this.mob.position().x, this.mob.position().y, this.mob.position().z, 1.0D, 0.0D, 0.0D);
        this.monster.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, this.monster.getEntity().getLocation(), 10);
        this.monster.getEntity().getWorld().getNearbyEntities(this.monster.getEntity().getLocation(), 4, 2, 4, entity -> entity instanceof Player)
                .forEach(entity -> ((org.bukkit.entity.LivingEntity) entity).damage(5, this.monster.getEntity()));
    }
}
