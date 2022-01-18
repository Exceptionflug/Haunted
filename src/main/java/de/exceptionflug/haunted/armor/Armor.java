package de.exceptionflug.haunted.armor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public class Armor {

    @Getter
    private ArmorType type;
    private EquipmentSlot slot;


    private Material getMaterial() {
        return switch (slot) {
            case HEAD -> type.getHelmet();
            case CHEST -> type.getChestplate();
            case LEGS -> type.getLeggings();
            case FEET -> type.getBoots();
            default -> throw new IllegalStateException("Unexpected value: " + slot);
        };
    }

    private ItemStack createItem() {
        ItemStack itemStack = new ItemStack(getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(type.getName());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void giveArmor(PlayerInventory inventory) {
        inventory.setItem(slot, createItem());
    }
}
