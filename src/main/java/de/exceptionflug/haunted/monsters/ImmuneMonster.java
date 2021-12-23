package de.exceptionflug.haunted.monsters;

import org.bukkit.entity.Projectile;

public interface ImmuneMonster {
    boolean isImmuneTo(Projectile projectile);
}
