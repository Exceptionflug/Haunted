package de.exceptionflug.haunted.npc;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Pose;

/**
 * Date: 17.08.2021
 *
 * @author Exceptionflug
 */
@SuppressWarnings("UnstableApiUsage")
public class NPC {

    private final Location location;
    private final PlayerProfile playerProfile;
    private Mannequin mannequin;

    public NPC(Location location, PlayerProfile playerProfile) {
        this.location = location;
        this.playerProfile = playerProfile;
    }

    public void spawn() {
        mannequin = (Mannequin)location.getWorld().spawnEntity(location, EntityType.MANNEQUIN);
        mannequin.setPose(Pose.SLEEPING);
        mannequin.setAI(false);
        mannequin.setProfile(ResolvableProfile.resolvableProfile(playerProfile));
    }

    public void despawn() {
        if (mannequin != null) {
            mannequin.remove();
        }
    }
}
