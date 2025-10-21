package de.exceptionflug.haunted.armor;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.shop.Shop;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;

public class ArmorShop implements Shop {


    private final int id;
    private final ArmorType armorType;
    private final int price;
    private final Location location;
    private final Location buttonLocation;

    public ArmorShop(int id, ArmorType armorType, int price, Location location, Location buttonLocation) {
        this.id = id;
        this.armorType = armorType;
        this.price = price;
        this.location = location;
        this.buttonLocation = buttonLocation;
    }

    @Override
    public void spawn() {

    }

    @Override
    public void despawn() {

    }

    @Override
    public Location triggerLocation() {
        return buttonLocation;
    }

    @Override
    public boolean interact(HauntedPlayer player) {
        if (player.gold() < price) {
            player.i18n().sendMessage(player.handle(), "Messages.canNotAfford", c -> {
                c.setDefaultMessage(() -> "§cDu kannst dir das nicht leisten!");
            });
            return false;
        }
        if (player.helmetArmor() != null) {
            if (player.helmetArmor().getType().ordinal() > armorType.ordinal()) {
                player.i18n().sendMessage(player.handle(), "Messages.armor.hasStronger", c -> {
                    c.setDefaultMessage(() -> "§cDu hast bereits bessere Rüstung!");
                });
                return false;
            }
            if (player.helmetArmor().getType().ordinal() == armorType.ordinal()) {
                player.i18n().sendMessage(player.handle(), "Messages.armor.hasSame", c -> {
                    c.setDefaultMessage(() -> "§cDu hast bereits diese Rüstung!");
                });
                return false;
            }
        }
        player.helmetArmor(new Armor(armorType, EquipmentSlot.HEAD));
        player.chestplateArmor(new Armor(armorType, EquipmentSlot.CHEST));
        player.leggingsArmor(new Armor(armorType, EquipmentSlot.LEGS));
        player.bootsArmor(new Armor(armorType, EquipmentSlot.FEET));

        player.gold(player.gold() - price);
        return true;
    }
}
