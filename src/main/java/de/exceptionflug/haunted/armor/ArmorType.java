package de.exceptionflug.haunted.armor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum ArmorType {
    LEATHER("§6Leder Rüstung", Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS),
    GOLD("§eGold Rüstung", Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
    CHAIN("§7Ketten Rüstung", Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
    IRON("§rEisen Rüstung", Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
    DIAMOND("§bDiamant Rüstung", Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
    NETHERITE("§cNetherite Rüstung", Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);

    private final String name;
    private final Material helmet;
    private final Material chestplate;
    private final Material leggings;
    private final Material boots;
}
