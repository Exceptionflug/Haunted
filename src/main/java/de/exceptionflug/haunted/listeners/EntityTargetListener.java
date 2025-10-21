package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

@Singleton
public class EntityTargetListener implements Listener {

    private final GameContext context;

    @Inject
    public EntityTargetListener(GameContext gameContext) {
        this.context = gameContext;

        Bukkit.getScheduler().runTaskTimer(context.plugin(), () -> {
            if (context.phase() instanceof HauntedIngamePhase phase) {
                long time = System.currentTimeMillis() - 10 * 1000;
                phase.wave().entities().values().forEach(monster -> {
                    if (monster.getLastSuccessfulInteraction() > time) {
                        monster.updateTarget(true);
                        monster.successfulInteraction();
                    }
                });
            }
        }, 20, 20);
    }

    @EventHandler
    public void onTargetSwitch(EntityTargetEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.FORGOT_TARGET) {
            Monster monster = context.<HauntedIngamePhase>phase().wave().monsterByEntity(event.getEntity());
            monster.updateTarget();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = null;
        if (event.getEntity() instanceof Player) entity = event.getDamager();
        if (event.getDamager() instanceof Player) entity = event.getEntity();
        if (entity != null) {
            if (!context.phase().ingamePhase()) return;
            Monster monster = context.<HauntedIngamePhase>phase().wave().monsterByEntity(event.getEntity());
            if (monster != null) {
                monster.successfulInteraction();
            }
        }
    }
}
