package de.cytooxien.haunted.game;

import de.cytooxien.strider.game.player.GamePlayer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
@Accessors(fluent = true)
public class HauntedPlayer extends GamePlayer<HauntedGame, HauntedTeam, HauntedPlayer> {

    @Getter
    private Location deathLocation;
    private int respawnTimer = 30;
    @Getter
    private boolean dead;
    @Getter
    private boolean revivable;

    public HauntedPlayer(HauntedGame game, UUID uuid) {
        super(game, uuid);
    }

    @Override
    public HauntedPlayer getInstance() {
        return this;
    }

    public void playerDied() {
        deathLocation = getLocation();
        dead = true;
        revivable = true;
        respawnTimer = 30;
        setSpectator(true);
        sendTitle("§7Du bist gestorben!", "§eDu kannst wiederbelebt werden", 10, 40, 10);
        setExp(1);
        setLevel(30);
    }

    public void revive() {
        if (!dead || !revivable) {
            return;
        }
        dead = false;
        revivable = false;
        setSpectator(false);
        sendTitle("§aDu wurdest gerettet!", "", 10, 40, 10);
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

}
