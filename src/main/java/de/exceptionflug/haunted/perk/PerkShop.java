package de.exceptionflug.haunted.perk;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.shop.Shop;
import org.bukkit.Location;

import java.util.List;

public class PerkShop implements Shop {


    private final int id;
    private final PerkType perkType;
    private final List<Integer> prices;
    private final int maxLevel;
    private final Location location;
    private final Location buttonLocation;

    public PerkShop(int id, PerkType perkType, List<Integer> prices, Location location, Location buttonLocation) {
        this.id = id;
        this.perkType = perkType;
        this.prices = prices;
        this.maxLevel = prices.size();
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
