package de.exceptionflug.haunted.weapon;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Date: 17.09.2021
 *
 * @author Exceptionflug
 */
public class GunShop implements Shop {

    private final int id;
    private final GunType gunType;
    private final int weaponPrice;
    private final int ammoPrice;
    private final Location location;

    private Hologram hologram;

    public GunShop(int id, GunType gunType, int weaponPrice, int ammoPrice, Location location) {
        this.id = id;
        this.gunType = gunType;
        this.weaponPrice = weaponPrice;
        this.ammoPrice = ammoPrice;
        this.location = location;
    }

    @Override
    public void spawn() {
        hologram = Holograms.createHologram(location);
        hologram.appendLine("§6" + gunType.displayName());
        hologram.appendLine(new ItemStack(gunType.itemType()));
        hologram.appendLine("§7Preis: §6" + weaponPrice + " Gold");
    }

    @Override
    public void interact(HauntedPlayer player) {

    }

}
