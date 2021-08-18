package de.exceptionflug.haunted.game;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.destroystokyo.paper.Title;
import com.mojang.authlib.GameProfile;
import de.exceptionflug.haunted.npc.NPC;
import de.exceptionflug.haunted.perk.Perk;
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
import de.exceptionflug.mccommons.scoreboards.Scoreboards;
import de.exceptionflug.mccommons.scoreboards.localized.LocalizedConfigBoard;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.File;


/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class HauntedPlayer extends GamePlayer {

    private static final SpigotConfig SCOREBOARD_CONFIG = ConfigFactory.create(new File("plugins/Haunted/scoreboard.yml"), SpigotConfig.class);
    private LocalizedConfigBoard scoreboard;
    private Location deathLocation;
    private int respawnTimer = 30;
    @Setter
    private int gold;
    @Setter
    private int kills;
    private boolean dead;
    private boolean revivable;
    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;
    private Perk primaryPerk;
    private Perk secondaryPerk;
    private Hologram sneakHologram;
    private TextHologramLine secondsLine;
    private NPC body;

    public HauntedPlayer(Player handle, GameContext context) {
        super(handle, context);
        scoreboard = new LocalizedConfigBoard(SCOREBOARD_CONFIG);
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
        primaryWeapon = new Gun(GunType.PISTOL, this, context);
        secondaryWeapon = new Gun(GunType.POSEIDONS_REVENGE, this, context);
    }

    public void playerDied() {
        deathLocation = getLocation();
        dead = true;
        revivable = context().alivePlayers().size() > 1;
        respawnTimer = 30;
        setSpectator(false);
        if (revivable) {
            Message.broadcast(context().players(), context().messageConfiguration(), "Messages.playerUnconscious", "§6%player% §7ist außer Gefecht! Du hast 30 Sekunden Zeit ihn oder sie wiederzubeleben.", "%player%", getName());
            getPlayer().sendTitle(new Title("§7Du bist außer Gefecht!", "§eDu kannst noch wiederbelebt werden", 10, 40, 10));
            context().players().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
            createLayingBody(deathLocation.clone().add(0, 0.2, 0));
        }
        setExp(1);
        setLevel(30);
        setHealth(20);
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

    public void revive() {
        if (!dead || !revivable) {
            return;
        }
        dead = false;
        revivable = false;
        setAlive(false);
        teleport(deathLocation);
        getPlayer().sendTitle(new Title("§aDu wurdest gerettet!", "", 10, 40, 10));
        removeLayingBody();
    }

    private void removeLayingBody() {
        body.despawn();
        sneakHologram.despawn();
    }

    public void giveGold(int gold) {
        this.gold += gold;
    }

    @Override
    public void giveIngameItems() {
        if (primaryWeapon != null) {
            primaryWeapon.give(this, 0);
        }
        if (secondaryWeapon != null) {
            secondaryWeapon.give(this, 1);
        }
    }

    @Override
    public void setIngameScoreboard() {
        Scoreboards.show(this, scoreboard.getScoreboard());
    }

    public void primaryWeapon(Weapon gun) {
        if (!spectator()) {
            primaryWeapon = gun;
        }
        giveIngameItems();
    }

    public void secondaryWeapon(Weapon gun) {
        if (!spectator()) {
            secondaryWeapon = gun;
        }
        giveIngameItems();
    }

    public void update() {
        scoreboard.update();
        if (dead && revivable) {
            respawnTimer --;
            setLevel(respawnTimer);
            String color = respawnTimer < 10 ? "§c" : "§e";
            secondsLine.setText(color + respawnTimer + "s");
            if (respawnTimer != 0) {
                setExp(respawnTimer / 30F);
            } else {
                revivable = false;
                sneakHologram.despawn();
                getPlayer().sendTitle(new Title("§cDu bist gestorben!", "§7Du wirst am Anfang der nächsten Welle wiederbelebt", 10, 40, 10));
                Message.broadcast(context().players(), context().messageConfiguration(), "Messages.playerDied", "§6%player% §7ist gestorben! Er oder sie wird am Anfang der nächsten Welle wiederbelebt.", "%player%", getName());
                context().players().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0));
            }
        }
    }
}
