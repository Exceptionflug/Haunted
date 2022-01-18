package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.monster.GateMonster;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;

public class WitherSkeletonMonster extends GateMonster {

    private WitherSkeleton witherSkeleton;

    @Override
    public void spawn(Location location) {
        witherSkeleton = (WitherSkeleton) EntityUtils.spawnCleanEntity(location, EntityType.WITHER_SKELETON);
        super.spawn(witherSkeleton, location);
        witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
    }
}
