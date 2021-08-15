package de.exceptionflug.haunted.monster;

import org.bukkit.entity.Entity;

import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public interface BossMonster extends Monster {

    List<Entity> allies();

}
