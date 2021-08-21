package de.exceptionflug.haunted.weapon;

import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.feature.hotbar.HotbarItemComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 12.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class Gun implements Weapon {

    public static final NamespacedKey HEADSHOT_KEY = NamespacedKey.fromString("headshot");
    public static final NamespacedKey DAMAGE_KEY = NamespacedKey.fromString("damage");
    public static final NamespacedKey SHOOTER_KEY = NamespacedKey.fromString("shooter");

    private final GunType gunType;
    private final ItemStack item;
    private final HauntedPlayer player;
    private final GameContext gameContext;

    @Setter
    private int ammunition;
    @Setter
    private int rounds;
    private boolean reloading;
    private long lastShot;
    private int slot;

    public Gun(GunType gunType, HauntedPlayer player, GameContext gameContext) {
        this.gunType = gunType;
        this.ammunition = gunType.maxAmmunition();
        this.rounds = gunType.rounds();
        this.player = player;
        this.gameContext = gameContext;
        this.item = createItem();
    }

    private ItemStack createItem() {
        ItemStack itemStack = new ItemStack(gunType.itemType());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§6" + gunType.displayName() + " (" + rounds + "/" + ammunition + ")");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void shoot(int slot) {
        if ((System.currentTimeMillis() - lastShot) < gunType.fireDelay() || reloading) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(gameContext.plugin(), () -> {
            for (int i = 0; i < gunType().volley(); i++) {
                Bukkit.getScheduler().runTask(gameContext.plugin(), () -> {
                    if (rounds == 0) {
                        reload();
                        return;
                    }
                    this.slot = slot;
                    lastShot = System.currentTimeMillis();
                    rounds--;
                    player.getInventory().setItem(slot, updateItem());
                    player.getWorld().playSound(player.getLocation(), gunType.shootSound(), 1, gunType.shootSoundPitch());
                    for (int j = 0; j < gunType().bulletsPerShot(); j++) {
                        launchProjectile();
                    }
                    if (gunType.recoil() > 0) {
                        Vector direction = player.getLocation().getDirection();
                        direction.setY(0);
                        player.setVelocity(direction.multiply(gunType.recoil() * -1));
                    }
                    if (rounds == 0 && ammunition > 0) {
                        reload();
                    }
                });
                if (gunType().volleyDelay() > 0) {
                    try {
                        Thread.sleep(gunType.volleyDelay());
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    private void launchProjectile() {
        Vector direction = player.getLocation().getDirection();
        if (gunType.spread() != 0) {
            double spreadX = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            double spreadY = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            double spreadZ = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            direction.add(new Vector(spreadX, spreadY, spreadZ));
        }
        Projectile projectile = player.launchProjectile(gunType.projectileType(), direction.multiply(gunType.speed()));
        projectile.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.DOUBLE, gunType.damage());
        projectile.getPersistentDataContainer().set(SHOOTER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
        if (projectile instanceof AbstractArrow) {
            ((AbstractArrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ((AbstractArrow) projectile).setDamage(gunType.damage());
        }
        if (projectile instanceof Explosive explosive) {
            explosive.setYield((float) gunType.damage());
        }
        if (projectile instanceof ThrownPotion potion) {
            ItemStack potionStack = new ItemStack(gunType.projectileType().equals(LingeringPotion.class) ? Material.LINGERING_POTION : Material.SPLASH_POTION);
            PotionMeta meta = (PotionMeta) potionStack.getItemMeta();
            meta.setColor(Color.RED);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 2, 2, true, true, false), true);
            potionStack.setItemMeta(meta);
            potion.setItem(potionStack);
        }
    }

    public void reload() {
        if (reloading) {
            return;
        }
        if (rounds == gunType().rounds()) {
            return;
        }
        if (ammunition == 0) {
            player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 2, 2);
            return;
        }
        reloading = true;
        player.getInventory().setItem(slot, updateItem());
        player.getWorld().playSound(player.getLocation(), gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        Bukkit.getScheduler().runTaskLater(gameContext.plugin(), () -> {
            int needed = gunType().rounds() - rounds;
            rounds = ammunition >= needed ? rounds + needed : rounds + ammunition;
            ammunition -= rounds;
            reloading = false;
            player.getInventory().setItem(slot, updateItem());
            player.getWorld().playSound(player.getLocation(), gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        }, gunType().reloadDelay());
        for (int i = 0; i < gunType.reloadDelay(); i++) {
            int finalI = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    double progress = (double)finalI / (double)gunType.reloadDelay();
                    ItemStack itemStack = updateItem();
                    ItemMeta meta = itemStack.getItemMeta();
                    if (meta instanceof Damageable damage) {
                        damage.setDamage((itemStack.getType().getMaxDurability()-1) - (int) (itemStack.getType().getMaxDurability() * progress));
                    }
                    itemStack.setItemMeta(meta);
                    player.getInventory().setItem(slot, itemStack);
                }
            }.runTaskLater(gameContext().plugin(),i);
        }
    }

    private ItemStack updateItem() {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + gunType.displayName() + " (" + rounds + "/" + ammunition + ") " + (reloading ? "§cя" : ""));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void give(HauntedPlayer player, int slot) {
        HotbarItemComponent.addItem(player, slot, item, (pp, event) -> {
            if (player.spectator()) {
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                shoot(slot);
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                reload();
            }
        });
    }

}
