package de.exceptionflug.haunted.game;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import de.exceptionflug.haunted.EntityUtils;
import de.exceptionflug.haunted.armor.Armor;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.npc.NPC;
import de.exceptionflug.haunted.perk.Perk;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.section.MapSection;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.haunted.weapon.Weapon;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.mccommons.holograms.Hologram;
import de.exceptionflug.mccommons.holograms.Holograms;
import de.exceptionflug.mccommons.holograms.line.TextHologramLine;
import de.exceptionflug.mccommons.inventories.spigot.item.ItemBuilder;
import de.exceptionflug.mccommons.scoreboards.Objective;
import de.exceptionflug.mccommons.scoreboards.Scoreboards;
import de.exceptionflug.mccommons.scoreboards.localized.LocalizedConfigBoard;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.function.Consumer;


/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class HauntedPlayer extends GamePlayer {

    private static final ItemStack PLACEHOLDER_GUN = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setTitle("§8").build();
    private static final ItemStack PLACEHOLDER_MAGIC = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setTitle("§8").build();
    private static final ItemStack PLACEHOLDER_PERK = new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE).setTitle("§8").build();
    private static final ItemStack PLACEHOLDER = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setTitle("§8").build();
    private static final ItemStack FORBIDDEN = new ItemBuilder(Material.BARRIER).setTitle("§cNicht freigeschaltet").build();
    private static final SpigotConfig SCOREBOARD_CONFIG = ConfigFactory.create(new File("plugins/Haunted/scoreboard.yml"), SpigotConfig.class);
    private final InternationalizationContext i18n;
    private final LocalizedConfigBoard scoreboard;
    private Objective goldObjective;
    private Objective healthObjective;
    private Location deathLocation;
    private int reviveTicks = 60;
    private int respawnTimer = 30;
    @Setter
    private int gold;
    @Setter
    private int kills;
    @Setter
    private int weaponSlots = 2;
    @Setter
    private int perkSlots = 2;
    private boolean dead;
    private boolean revivable;
    private boolean beeingRevived;
    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    private Weapon thirdWeapon;
    private Perk primaryPerk;
    private Perk secondaryPerk;
    private Perk thirdPerk;
    private Armor helmetArmor;
    private Armor chestplateArmor;
    private Armor leggingsArmor;
    private Armor bootsArmor;
    private Hologram sneakHologram;
    private TextHologramLine secondsLine;
    private NPC body;

    public HauntedPlayer(Player handle, GameContext context) {
        super(handle, context);
        i18n = context.injector().getInstance(InternationalizationContext.class);
        scoreboard = new LocalizedConfigBoard(SCOREBOARD_CONFIG);
        goldObjective = scoreboard.getScoreboard().registerNewObjective("gold", "dummy");
        goldObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        healthObjective = scoreboard.getScoreboard().registerNewObjective("health", "health");
        healthObjective.setDisplayName("§c❤");
        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        scoreboard.format("%gold%", abstractBoardHolder -> Integer.toString(gold));
        scoreboard.format("%kills%", abstractBoardHolder -> Integer.toString(kills));
        scoreboard.format("%section%", abstractBoardHolder -> {
            MapSection section = context.<HauntedMap>currentMap().mapSection(handle().getLocation());
            if (section == null) {
                return "N/A";
            } else {
                return section.displayName();
            }
        });
        scoreboard.format("%wave%", abstractBoardHolder -> {
            if (!context.phase().ingamePhase()) {
                return "0";
            }
            return Integer.toString(context().<HauntedIngamePhase>phase().currentWave());
        });
        scoreboard.format("%remaining%", abstractBoardHolder -> {
            if (!context.phase().ingamePhase()) {
                return "0";
            }
            HauntedIngamePhase phase = context.phase();
            if (phase.wave() != null) {
                return Integer.toString(phase.wave().remainingMonsters());
            }
            return "0";
        });
        primaryWeapon = new Gun(GunType.PISTOL, this, context);
        prefix(Component.empty().color(NamedTextColor.GRAY));
    }

    public void playerDied() {
        deathLocation = handle().getLocation();
        dead = true;
        revivable = context().alivePlayers().size() > 1;
        respawnTimer = 30;
        setSpectator(false);
        if (revivable) {
            i18n.broadcast(context().players(), "Messages.playerUnconscious", c -> {
                c.setDefaultMessage(() -> "§6%player% §7ist außer Gefecht! Du hast 30 Sekunden Zeit ihn oder sie wiederzubeleben.");
                c.setArgument("player", handle().getName());
            });
            handle().sendTitle("§7Du bist außer Gefecht!", "§eDu kannst noch wiederbelebt werden", 10, 40, 10);
            context().players().forEach(player -> player.handle().playSound(player.handle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
            createLayingBody(deathLocation.clone().add(0, 0.2, 0));
        }
        handle().setExp(1);
        handle().setLevel(30);
        handle().setHealth(20);
        Bukkit.getScheduler().runTaskLater(context().plugin(), () -> handle().spigot().respawn(), 10);
    }

    public void createLayingBody(Location location) {
        body = new NPC(location, (GameProfile) WrappedGameProfile.fromPlayer(handle()).getHandle());
        body.spawn();
        Bukkit.getScheduler().runTaskAsynchronously(context().plugin(), () -> {
            sneakHologram = Holograms.createHologram(location.add(0, 0.5, 0));
            sneakHologram.appendLine("§7Schleichen zum §cWiederbeleben");
            secondsLine = sneakHologram.appendLine("§e30s");
            sneakHologram.spawn();
        });
    }

    public void revive(HauntedPlayer hauntedPlayer) {
        if (!dead || !revivable) {
            return;
        }
        if (beeingRevived) {
            return;
        }
        beeingRevived = true;
        new BukkitRunnable() {

            private int timer;

            @Override
            public void run() {
                Player player = hauntedPlayer.handle();
                timer++;
                if (!revivable) {
                    beeingRevived = false;
                    cancel();
                    return;
                }
                if (!player.isSneaking()) {
                    beeingRevived = false;
                    cancel();
                    return;
                }
                player.sendTitle("§8[" + buildProgressbar(40, timer / (float) reviveTicks, "§a") + "§8]", "§7Halte die Taste gedrückt", 0, 6, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, (timer / (float) (reviveTicks / 1.5)) + 0.5F);
                if (timer == reviveTicks) {
                    cancel();
                    respawn();
                    handle().sendTitle("§aDu wurdest gerettet!", "", 10, 40, 10);
                }
            }

        }.runTaskTimer(context().plugin(), 1, 1);
    }

    public void respawn() {
        beeingRevived = false;
        dead = false;
        revivable = false;
        setAlive(false);
        handle().teleport(deathLocation);
        removeLayingBody();
    }

    private String buildProgressbar(int length, float percentage, String color) {
        var filledChars = (int) (length * percentage);
        var stringBuilder = new StringBuilder(color);
        for (int i = 0; i < length; i++) {
            if (i <= filledChars) {
                stringBuilder.append("|");
            } else {
                stringBuilder.append("§7|");
            }
        }
        return stringBuilder.toString();
    }

    private void removeLayingBody() {
        body.despawn();
        sneakHologram.despawn();
    }

    public void giveGold(int gold) {
        this.gold += gold;
        //sendMessage("§6+ " + gold + " Gold");
    }

    @Override
    public void giveIngameItems() {
        if (primaryWeapon != null) {
            primaryWeapon.give(this, 1);
        }
        if (secondaryWeapon != null) {
            secondaryWeapon.give(this, 2);
        } else handle().getInventory().setItem(2, PLACEHOLDER_GUN);
        if (weaponSlots == 3) {
            if (thirdWeapon != null) {
                thirdWeapon.give(this, 3);
            } else handle().getInventory().setItem(3, PLACEHOLDER_GUN);
        }
        handle().getInventory().setItem(4, PLACEHOLDER_MAGIC);
        if (primaryPerk() != null) {
            primaryPerk.give(this, 6);
        } else handle().getInventory().setItem(6, PLACEHOLDER_PERK);
        if (secondaryPerk() != null) {
            secondaryPerk.give(this, 7);
        } else handle().getInventory().setItem(7, PLACEHOLDER_PERK);
        if (thirdPerk() != null && perkSlots == 3) {
            thirdPerk.give(this, 8);
        } else handle().getInventory().setItem(8, PLACEHOLDER_PERK);
        Consumer<Armor> armorConsumer = armor -> {
            if (armor != null) armor.giveArmor(handle().getInventory());
        };
        armorConsumer.accept(helmetArmor());
        armorConsumer.accept(chestplateArmor());
        armorConsumer.accept(leggingsArmor());
        armorConsumer.accept(bootsArmor());
    }

    @Override
    public void setIngameScoreboard() {
        Scoreboards.show(handle(), scoreboard.getScoreboard());
    }

    public void primaryWeapon(Weapon gun) {
        if (!spectator()) {
            if (primaryWeapon != null) {
                primaryWeapon.destroy();
            }
            primaryWeapon = gun;
        }
        giveIngameItems();
    }

    public void secondaryWeapon(Weapon gun) {
        if (!spectator()) {
            if (secondaryWeapon != null) {
                secondaryWeapon.destroy();
            }
            secondaryWeapon = gun;
        }
        giveIngameItems();
    }

    public void thirdWeapon(Weapon gun) {
        if (!spectator()) {
            if (thirdWeapon != null) {
                thirdWeapon.destroy();
            }
            thirdWeapon = gun;
        }
        giveIngameItems();
    }

    public void primaryPerk(Perk perk) {
        if (!spectator()) {
            primaryPerk = perk;
        }
        giveIngameItems();
    }

    public void secondaryPerk(Perk perk) {
        if (!spectator()) {
            secondaryPerk = perk;
        }
        giveIngameItems();
    }

    public void thirdPerk(Perk perk) {
        if (!spectator()) {
            thirdPerk = perk;
        }
        giveIngameItems();
    }

    public void helmetArmor(Armor armor) {
        if (!spectator()) {
            helmetArmor = armor;
        }
        giveIngameItems();
    }

    public void chestplateArmor(Armor armor) {
        if (!spectator()) {
            chestplateArmor = armor;
        }
        giveIngameItems();
    }

    public void leggingsArmor(Armor armor) {
        if (!spectator()) {
            leggingsArmor = armor;
        }
        giveIngameItems();
    }

    public void bootsArmor(Armor armor) {
        if (!spectator()) {
            bootsArmor = armor;
        }
        giveIngameItems();
    }

    public void update() {
        scoreboard.update();
        checkRepairing();
        updateTabScores();
        if (dead && revivable) {
            respawnTimer--;
            handle().setLevel(respawnTimer);
            String color = respawnTimer < 10 ? "§c" : "§e";
            secondsLine.setText(color + respawnTimer + "s");
            if (respawnTimer != 0) {
                handle().setExp(respawnTimer / 30F);
            } else {
                revivable = false;
                sneakHologram.despawn();
                handle().sendTitle("§cDu bist gestorben!", "§7Du wirst am Anfang der nächsten Welle wiederbelebt", 10, 40, 10);
                i18n.broadcast(context().players(), "Messages.playerDied", c -> {
                    c.setDefaultMessage(() -> "§6%player% §7ist gestorben! Er oder sie wird am Anfang der nächsten Welle wiederbelebt.");
                    c.setArgument("player", handle().getName());
                });
                context().players().forEach(player -> player.handle().playSound(player.handle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0));
            }
        }
    }

    private void updateTabScores() {
        for (HauntedPlayer player : context().<HauntedPlayer>players()) {
            goldObjective.getScore(player.handle()).setScore(player.gold());
        }
    }

    private void checkRepairing() {
        HauntedMap hauntedMap = context().currentMap();
        MobGate mobGate = hauntedMap.mobGateByRepairZone(handle().getLocation());
        if (mobGate != null && handle().isSneaking() && !spectator()) {
            if (context().<HauntedIngamePhase>phase().wave() != null) {
                for (Monster monster : context().<HauntedIngamePhase>phase().wave().entities().values()) {
                    LivingEntity entity = monster.getEntity();
                    if (!entity.isDead() && entity.getLocation().distance(handle().getLocation()) < 6) {
                        i18n.sendMessage(handle(), "Messages.monstersNearby", c -> {
                            c.setDefaultMessage(() -> "§cEs sind Monster in der Nähe.");
                        });
                        return;
                    }
                }
            }
            if (mobGate.repairingPlayer() != null) {
                if (!mobGate.repairingPlayer().getUniqueId().equals(handle().getUniqueId())) {
                    return;
                }
            }
            if (mobGate.repaired()) {
                return;
            }
            mobGate.repairingPlayer(handle().getPlayer());
            Location location = mobGate.repairGate(1);
            if (location != null) {
                giveGold(10);
                EntityUtils.spawnPointsHologram(location.clone().add(0.5, 0, 0.5), "§6+ 10 Gold");
            }
            if (mobGate.repaired()) {
                mobGate.repairingPlayer(null);
                handle().playSound(handle().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            }
        } else {
            hauntedMap.mobGates().forEach(it -> {
                if (it.repairingPlayer() != null && it.repairingPlayer().getUniqueId().equals(handle().getUniqueId())) {
                    it.repairingPlayer(null);
                }
            });
        }
    }

    public InternationalizationContext i18n() {
        return i18n;
    }

}
