package de.exceptionflug.haunted.phases;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.phases.IngamePhase;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
public class HauntedIngamePhase extends IngamePhase {

    private long startedSince;
    private BukkitTask task;

    @Inject
    public HauntedIngamePhase(GameContext context) {
        super(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        startedSince = System.currentTimeMillis();
        startScoreboardUpdater();
    }

    @Override
    public void onStop() {
        super.onStop();
        task.cancel();
    }

    private void startScoreboardUpdater() {
        context().<HauntedPlayer>players().forEach(player -> {
            player.scoreboard().format("%time%", h -> DurationFormatUtils.formatDuration(System.currentTimeMillis() - startedSince, "mm:ss", true));
        });
        task = Bukkit.getScheduler().runTaskTimer(context().plugin(), () -> {
            context().<HauntedPlayer>players().forEach(player -> {
                player.scoreboard().update();
            });
        }, 20, 20);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onEggPop(ThrownEggHatchEvent event) {
        event.setHatching(false);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
        if (event.getEntity().getKiller() != null) {
            HauntedPlayer player = context().player(event.getEntity().getKiller());
            if (player != null) {
                player.kills(player.kills() + 1);
                player.scoreboard().update();
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
