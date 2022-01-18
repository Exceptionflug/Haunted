package de.exceptionflug.haunted.armor;

import lombok.AllArgsConstructor;
import org.bukkit.inventory.EquipmentSlot;

@AllArgsConstructor
public enum ArmorPiece {
    LEATHER_HELMET(ArmorType.LEATHER, EquipmentSlot.HEAD);

    private final ArmorType type;
    private final EquipmentSlot slot;
}
