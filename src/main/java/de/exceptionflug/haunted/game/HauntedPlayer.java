package de.exceptionflug.haunted.game;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import de.exceptionflug.haunted.EntityUtils;
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
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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


/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class HauntedPlayer extends GamePlayer {

    private static final ItemStack PLACEHOLDER = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setTitle("§8").build();
    private static final ItemStack FORBIDDEN = new ItemBuilder(Material.BARRIER).setTitle("§cNicht freigeschaltet").build();
    private static final SpigotConfig SCOREBOARD_CONFIG = ConfigFactory.create(new File("plugins/Haunted/scoreboard.yml"), SpigotConfig.class);
    private LocalizedConfigBoard scoreboard;
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
    private Hologram sneakHologram;
    private TextHologramLine secondsLine;
    private NPC body;

    public HauntedPlayer(Player handle, GameContext context) {
        super(handle, context);
        scoreboard = new LocalizedConfigBoard(SCOREBOARD_CONFIG);
        goldObjective = scoreboard.getScoreboard().registerNewObjective("gold", "dummy");
        goldObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        healthObjective = scoreboard.getScoreboard().registerNewObjective("health", "health");
        healthObjective.setDisplayName("§c❤");
        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        scoreboard.format("%gold%", abstractBoardHolder -> Integer.toString(gold));
        scoreboard.format("%kills%", abstractBoardHolder -> Integer.toString(kills));
        scoreboard.format("%section%", abstractBoardHolder -> {
            MapSection section = context.<HauntedMap>currentMap().mapSection(getLocation());
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
        colorPrefix("§7");
    }

    public void playerDied() {
        deathLocation = getLocation();
        dead = true;
        revivable = context().alivePlayers().size() > 1;
        respawnTimer = 30;
        setSpectator(false);
        if (revivable) {
            Message.broadcast(context().players(), context().messageConfiguration(), "Messages.playerUnconscious", "§6%player% §7ist außer Gefecht! Du hast 30 Sekunden Zeit ihn oder sie wiederzubeleben.", "%player%", getName());
            getPlayer().sendTitle("§7Du bist außer Gefecht!", "§eDu kannst noch wiederbelebt werden", 10, 40, 10);
            context().players().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
            createLayingBody(deathLocation.clone().add(0, 0.2, 0));
        }
        setExp(1);
        setLevel(30);
        setHealth(20);
        Bukkit.getScheduler().runTaskLater(context().plugin(), () -> spigot().respawn(), 10);
    }

    public void createLayingBody(Location location) {
        body = new NPC(location, (GameProfile) WrappedGameProfile.fromPlayer(getPlayer()).getHandle());
        body.spawn();
        Bukkit.getScheduler().runTaskAsynchronously(context().plugin(), () -> {
            sneakHologram = Holograms.createHologram(location.add(0, 0.5, 0));
            sneakHologram.appendLine("§7Schleichen zum §cWiederbeleben");
            secondsLine = sneakHologram.appendLine("§e30s");
            sneakHologram.spawn();
        });
    }

    public void revive(HauntedPlayer player) {
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
                    getPlayer().sendTitle("§aDu wurdest gerettet!", "", 10, 40, 10);
                }
            }

        }.runTaskTimer(context().plugin(), 1, 1);
    }

    public void respawn() {
        beeingRevived = false;
        dead = false;
        revivable = false;
        setAlive(false);
        teleport(deathLocation);
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
            primaryWeapon.give(this, 0);
        }
        if (secondaryWeapon != null) {
            secondaryWeapon.give(this, 1);
        }
        if (thirdWeapon != null && weaponSlots == 3) {
            thirdWeapon.give(this, 2);
        } else if (weaponSlots < 3) {
            getInventory().setItem(2, FORBIDDEN);
        }
        getInventory().setItem(3, PLACEHOLDER);
        getInventory().setItem(4, PLACEHOLDER);
        getInventory().setItem(5, PLACEHOLDER);
        if (primaryPerk() != null) {
            primaryPerk.give(this, 8);
        }
        if (secondaryPerk() != null) {
            secondaryPerk.give(this, 7);
        }
        if (thirdPerk() != null && perkSlots == 3) {
            thirdPerk.give(this, 6);
        } else if (perkSlots < 3) {
            getInventory().setItem(6, FORBIDDEN);
        }
    }

    @Override
    public void setIngameScoreboard() {
        Scoreboards.show(this, scoreboard.getScoreboard());
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

    public void update() {
        scoreboard.update();
        checkRepairing();
        updateTabScores();
        if (dead && revivable) {
            respawnTimer--;
            setLevel(respawnTimer);
            String color = respawnTimer < 10 ? "§c" : "§e";
            secondsLine.setText(color + respawnTimer + "s");
            if (respawnTimer != 0) {
                setExp(respawnTimer / 30F);
            } else {
                revivable = false;
                sneakHologram.despawn();
                getPlayer().sendTitle("§cDu bist gestorben!", "§7Du wirst am Anfang der nächsten Welle wiederbelebt", 10, 40, 10);
                Message.broadcast(context().players(), context().messageConfiguration(), "Messages.playerDied", "§6%player% §7ist gestorben! Er oder sie wird am Anfang der nächsten Welle wiederbelebt.", "%player%", getName());
                context().players().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0));
            }
        }
    }

    private void updateTabScores() {
        for (HauntedPlayer player : context().<HauntedPlayer>players()) {
            goldObjective.getScore(player).setScore(player.gold());
        }
    }

    private void checkRepairing() {
        HauntedMap hauntedMap = context().currentMap();
        MobGate mobGate = hauntedMap.mobGateByRepairZone(getLocation());
        if (mobGate != null && isSneaking() && !spectator()) {
            if (context().<HauntedIngamePhase>phase().wave() != null) {
                for (Monster monster : context().<HauntedIngamePhase>phase().wave().entities().values()) {
                    LivingEntity entity = monster.getEntity();
                    if (!entity.isDead() && entity.getLocation().distance(getLocation()) < 6) {
                        Message.send(this, context().messageConfiguration(), "Messages.monstersNearby", "§cEs sind Monster in der Nähe.");
                        return;
                    }
                }
            }
            if (mobGate.repairingPlayer() != null) {
                if (!mobGate.repairingPlayer().getUniqueId().equals(getUniqueId())) {
                    return;
                }
            }
            if (mobGate.repaired()) {
                return;
            }
            mobGate.repairingPlayer(getPlayer());
            Location location = mobGate.repairGate(1);
            if (location != null) {
                giveGold(10);
                EntityUtils.spawnPointsHologram(location.clone().add(0.5, 0, 0.5), "§6+ 10 Gold");
            }
            if (mobGate.repaired()) {
                mobGate.repairingPlayer(null);
                playSound(getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
            }
        } else {
            hauntedMap.mobGates().forEach(it -> {
                if (it.repairingPlayer() != null && it.repairingPlayer().getUniqueId().equals(getUniqueId())) {
                    it.repairingPlayer(null);
                }
            });
        }
    }

    @Override
    public GameContext context() {
        return super.context();
    }

}
