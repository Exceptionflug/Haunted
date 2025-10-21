package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@Singleton
public class EntityDeathListener implements Listener {

    private final GameContext context;

    @Inject
    public EntityDeathListener(GameContext gameContext) {
        this.context = gameContext;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        HauntedIngamePhase phase = context.phase();
        Monster monster = phase.wave().monsterByEntity(event.getEntity());
        if (monster != null) {
            monster.handleDeath();
        }
    }
}
