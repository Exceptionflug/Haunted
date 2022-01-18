package de.exceptionflug.haunted.armor;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.shop.Shop;
import org.bukkit.Location;

import java.util.List;

public class MultipleArmorShop implements Shop {

    private final int id;
    private final List<List<ArmorType>> armorTypes;
    private int price;
    private final double priceMultiplier;
    private final Location location;
    private final Location buttonLocation;

    public MultipleArmorShop(int id, List<List<ArmorType>> armorTypes, int price, double priceMultiplier, Location location, Location buttonLocation) {
        this.id = id;
        this.armorTypes = armorTypes;
        this.price = price;
        this.priceMultiplier = priceMultiplier;
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
        return null;
    }

    @Override
    public boolean interact(HauntedPlayer player) {
        return false;
    }
}
