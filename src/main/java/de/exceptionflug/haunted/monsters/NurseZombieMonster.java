package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.ItemModifier;
import de.exceptionflug.haunted.util.ItemUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class NurseZombieMonster extends SpellCasterMonster {

    private Zombie zombie;

    @Override
    public void spawn(Location location) {
        zombie = (Zombie) EntityUtils.spawnCleanEntity(location, EntityType.ZOMBIE);
        super.spawn(zombie, location);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemUtils.setSkullTexture(head, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUyYzYyNTQ1ZWNmMWY5MjVmNzdmYzA5MTIzZTZlMjg0NzAzZjUzZjNkZWZkMWI3M2RhZjlkZDk0Njg3YTQzZiJ9fX0=");
        zombie.getEquipment().setHelmet(head);
        zombie.getEquipment().setChestplate(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.WHITE));
        zombie.getEquipment().setLeggings(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_LEGGINGS), Color.WHITE));
        zombie.getEquipment().setBoots(ItemModifier.dyeLeatherArmor(new ItemStack(Material.LEATHER_BOOTS), Color.WHITE));
    }

    @Override
    public void updateTarget(boolean force) {
        if (zombie.getTarget() != null && zombie.getLocation().distanceSquared(zombie.getTarget().getLocation()) < 9) return;
        List<LivingEntity> list = zombie.getLocation().getNearbyLivingEntities(16, 4, 16, e -> getPhase().wave().monsterByEntity(e) != null).stream().toList();
        if (!list.isEmpty()) {
            zombie.setTarget(list.get(new Random().nextInt(list.size())));
        }
    }

    @Override
    public void performSpellCasting() {
        // heal nearby allies
        System.out.println("HEAL");
        spawnParticle(Particle.HEART, 5);
    }
}
