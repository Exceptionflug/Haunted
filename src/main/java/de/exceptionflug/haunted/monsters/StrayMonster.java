package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Stray;

public class StrayMonster extends GateMonster implements ImmuneMonster {

    private Stray stray;

    @Override
    public void spawn(Location location) {
        stray = location.getWorld().spawn(location, Stray.class);
        stray.setCanPickupItems(false);
        this.shouldAddMeleeAttackGoal = false;
        super.spawn(stray, location);
        if (getNmsEntity() instanceof AbstractSkeleton abstractSkeleton) {
            abstractSkeleton.reassessWeaponGoal();
        }
    }

    @Override
    public boolean isImmuneTo(Projectile projectile) {
        return projectile.getClass().isAssignableFrom(Snowball.class);
    }
}
