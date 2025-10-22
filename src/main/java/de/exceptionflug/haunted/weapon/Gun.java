package de.exceptionflug.haunted.weapon;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.feature.hotbar.HotbarItemComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.*;
import org.bukkit.block.Block;
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
    @Setter
    private boolean destroyed;

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
                    if (destroyed) {
                        return;
                    }
                    this.slot = slot;
                    lastShot = System.currentTimeMillis();
                    rounds--;
                    player.handle().getInventory().setItem(slot, updateItem());
                    player.handle().getWorld().playSound(player.handle().getLocation(), gunType.shootSound(), 1, gunType.shootSoundPitch());
                    for (int j = 0; j < gunType().bulletsPerShot(); j++) {
                        launchProjectile();
                    }
                    if (gunType.recoil() > 0) {
                        Vector direction = player.handle().getLocation().getDirection();
                        direction.setY(0);
                        player.handle().setVelocity(direction.multiply(gunType.recoil() * -1));
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
        Vector direction = player.handle().getLocation().getDirection();
        if (gunType.spread() != 0) {
            double spreadX = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            double spreadY = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            double spreadZ = ThreadLocalRandom.current().nextDouble(-gunType().spread(), gunType().spread());
            direction.add(new Vector(spreadX, spreadY, spreadZ));
        }
        Projectile projectile = player.handle().launchProjectile(gunType.projectileType(), direction.multiply(gunType.speed()));
        projectile.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.DOUBLE, gunType.damage());
        projectile.getPersistentDataContainer().set(SHOOTER_KEY, PersistentDataType.STRING, player.handle().getUniqueId().toString());
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
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 2, 2, true, true, false), true);
            potionStack.setItemMeta(meta);
            potion.setItem(potionStack);
        }
    }

    public void reload() {
        if (reloading || destroyed) {
            return;
        }
        if (rounds == gunType().rounds()) {
            return;
        }
        if (ammunition == 0) {
            player.playSound(Sound.ITEM_FLINTANDSTEEL_USE, 2, 2);
            return;
        }
        reloading = true;
        player.handle().getInventory().setItem(slot, updateItem());
        player.handle().getWorld().playSound(player.handle().getLocation(), gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        Bukkit.getScheduler().runTaskLater(gameContext.plugin(), () -> {
            if (!gameContext.phase().ingamePhase() || destroyed) {
                return;
            }
            int needed = gunType().rounds() - rounds;
            rounds = ammunition >= needed ? rounds + needed : rounds + ammunition;
            ammunition -= rounds;
            reloading = false;
            player.handle().getInventory().setItem(slot, updateItem());
            player.handle().getWorld().playSound(player.handle().getLocation(), gunType.reloadSound(), 1, gunType.reloadSoundPitch());
        }, gunType().reloadDelay());

        new BukkitRunnable() {

            private int count = 0;

            @Override
            public void run() {
                if (!gameContext.phase().ingamePhase() || destroyed) {
                    cancel();
                    return;
                }
                if (count >= gunType.reloadDelay())
                    cancel();
                double progress = (double)count / (double)gunType.reloadDelay();
                ItemStack itemStack = updateItem();
                ItemMeta meta = itemStack.getItemMeta();
                if (meta instanceof Damageable damage) {
                    int calculatedDamage = (itemStack.getType().getMaxDurability()-1) - (int) (itemStack.getType().getMaxDurability() * progress);
                    if (calculatedDamage > 0) {
                        damage.setDamage(calculatedDamage);
                    }
                }
                itemStack.setItemMeta(meta);
                player.handle().getInventory().setItem(slot, itemStack);
                count++;
            }
        }.runTaskTimer(gameContext().plugin(),0,1);
    }

    public ItemStack updateItem() {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + gunType.displayName() + " (" + rounds + "/" + ammunition + ") " + (reloading ? "§cя" : ""));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void give(HauntedPlayer player, int slot) {
        HotbarItemComponent.addItem(player.handle(), slot, item, (pp, event) -> {
            if (player.spectator()) {
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock != null && (Tag.BUTTONS.isTagged(clickedBlock.getType()) || clickedBlock.getType() == Material.LEVER)) {
                    return; // Don't shoot at controls
                }
                if (clickedBlock != null && gameContext().<HauntedMap>currentMap().sectionGate(clickedBlock.getLocation()) != null) {
                    return; // Don't shoot at gates
                }
                shoot(slot);
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                reload();
            }
        });
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

}
