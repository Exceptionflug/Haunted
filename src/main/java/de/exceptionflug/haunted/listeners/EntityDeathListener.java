package de.exceptionflug.haunted.listeners;

import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@Component
public class EntityDeathListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        entity.getPassengers().forEach(Entity::remove);
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
            entity.remove();
        }
    }
}
