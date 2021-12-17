package de.exceptionflug.haunted.monsters;

import org.bukkit.Location;
import org.bukkit.entity.GlowSquid;

public class GlowSquidMonster extends ZombieMonster {

    private GlowSquid squid;

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        getZombie().setSilent(true);
        getZombie().setInvisible(true);
        squid = location.getWorld().spawn(location, GlowSquid.class);
        squid.setInvulnerable(true);
        squid.setCollidable(false);
        getZombie().addPassenger(squid);
    }

    @Override
    public void handleDeath() {
        despawnAllLinkedEntities();
    }
}
