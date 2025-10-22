package de.exceptionflug.haunted.weapon;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.shop.Shop;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

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
    private final Location buttonLocation;

    private Hologram hologram;

    public GunShop(int id, GunType gunType, int weaponPrice, int ammoPrice, Location location, Location buttonLocation) {
        this.id = id;
        this.gunType = gunType;
        this.weaponPrice = weaponPrice;
        this.ammoPrice = ammoPrice;
        this.location = location;
        this.buttonLocation = buttonLocation;
    }

    @Override
    public void spawn() {
        if (hologram != null) {
            if (!hologram.isDisabled()) {
                return;
            }
            hologram.enable();
            return;
        }
        hologram = DHAPI.createHologram("GunShop_" + UUID.randomUUID(),
                location,
                List.of(
                        "§b" + gunType.displayName(),
                        "", // Item
                        "§7Preis: §b" + weaponPrice + " Gold",
                        "§7(Rechtsklick auf Knopf)"
                    ));
        DHAPI.setHologramLine(hologram, 1, new ItemStack(gunType.itemType()));
    }

    @Override
    public void despawn() {
        hologram.disable();
    }

    @Override
    public Location triggerLocation() {
        return buttonLocation;
    }

    @Override
    public boolean interact(HauntedPlayer player) {
        if (player.gold() < weaponPrice) {
            player.i18n().sendMessage(player.handle(), "Messages.canNotAfford", c -> {
                c.setDefaultMessage(() -> "§cDu kannst dir das nicht leisten!");
            });
            return false;
        }
        Gun existingGun = null;
        if (player.primaryWeapon() instanceof Gun gun) {
            if (gun.gunType() == gunType) {
                existingGun = gun;
            }
        } else if (player.secondaryWeapon() instanceof Gun gun) {
            if (gun.gunType() == gunType) {
                existingGun = gun;
            }
        } else if (player.thirdWeapon() instanceof Gun gun) {
            if (gun.gunType() == gunType) {
                existingGun = gun;
            }
        }
        if (existingGun != null) {
            if (existingGun.reloading()) {
                player.i18n().sendMessage(player.handle(), "Messages.reloading", c -> {
                    c.setDefaultMessage(() -> "§cDeine Waffe wird gerade nachgeladen! Versuche es gleich nochmal.");
                });
                return false;
            }
            existingGun.ammunition(gunType.maxAmmunition());
            existingGun.rounds(gunType.rounds());
            player.handle().getInventory().setItem(existingGun.slot(), existingGun.updateItem());
            player.playSound(gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        } else {
            Gun gun = new Gun(gunType, player, player.context());
            if (player.primaryWeapon() == null) {
                player.primaryWeapon(gun);
            } else if (player.secondaryWeapon() == null) {
                player.secondaryWeapon(gun);
            } else if (player.weaponSlots() == 3 && player.thirdWeapon() == null) {
                player.thirdWeapon(gun);
            } else if (player.handle().getInventory().getHeldItemSlot() == 0) {
                player.primaryWeapon(gun);
            } else if (player.handle().getInventory().getHeldItemSlot() == 1) {
                player.secondaryWeapon(gun);
            } else if (player.handle().getInventory().getHeldItemSlot() == 2 && player.weaponSlots() == 3) {
                player.thirdWeapon(gun);
            } else {
                player.i18n().sendMessage(player.handle(), "Messages.selectWeaponSlot", c -> {
                    c.setDefaultMessage(() -> "§cBitte wähle einen Waffenslot aus!");
                });
                return false;
            }
            player.playSound(gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        }
        player.gold(player.gold() - weaponPrice);
        return true;
    }

}
