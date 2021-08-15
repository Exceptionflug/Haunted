package de.exceptionflug.haunted.game;

import com.destroystokyo.paper.Title;
import de.exceptionflug.haunted.perk.Perk;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.haunted.weapon.Weapon;
import de.exceptionflug.mccommons.config.shared.ConfigFactory;
import de.exceptionflug.mccommons.config.spigot.SpigotConfig;
import de.exceptionflug.mccommons.scoreboards.Scoreboards;
import de.exceptionflug.mccommons.scoreboards.localized.LocalizedConfigBoard;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Location;
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

    public HauntedPlayer(Player handle, GameContext context) {
        super(handle, context);
        scoreboard = new LocalizedConfigBoard(SCOREBOARD_CONFIG);
        scoreboard.format("%gold%", abstractBoardHolder -> Integer.toString(gold));
        scoreboard.format("%kills%", abstractBoardHolder -> Integer.toString(kills));
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
            getPlayer().sendTitle(new Title("§7Du bist gestorben!", "§eDu kannst wiederbelebt werden", 10, 40, 10));
        }
        setExp(1);
        setLevel(30);
        setHealth(20);
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
    }

    public void giveGold(int gold) {
        this.gold += gold;
    }

    public void gameTick() {
        if (dead && revivable) {
            respawnTimer --;
            if (respawnTimer == 0) {
                revivable = false;
            }
            setLevel(respawnTimer);
            setExp(respawnTimer / 30F);
        }
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

}
