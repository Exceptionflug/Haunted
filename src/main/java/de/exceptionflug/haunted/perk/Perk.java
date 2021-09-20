package de.exceptionflug.haunted.perk;

import de.exceptionflug.haunted.game.HauntedPlayer;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
public interface Perk {

    void enable();
    void disable();

    void give(HauntedPlayer player, int slot);
}
