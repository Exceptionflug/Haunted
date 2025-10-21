package de.exceptionflug.haunted.listeners;

import de.exceptionflug.projectvenom.game.aop.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Date: 21.09.2021
 *
 * @author Exceptionflug
 */
@Singleton
public final class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

}
