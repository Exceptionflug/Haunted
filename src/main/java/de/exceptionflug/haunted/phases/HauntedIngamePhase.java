package de.exceptionflug.haunted.phases;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import com.google.inject.Inject;
import de.exceptionflug.haunted.HauntedOptions;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.mccommons.config.spigot.Message;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.option.OptionComponent;
import de.exceptionflug.projectvenom.game.phases.IngamePhase;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

/**
 * Date: 10.08.2021
 *
 * @author Exceptionflug
 */
@Accessors(fluent = true)
public class HauntedIngamePhase extends IngamePhase {

    private long startedSince;
    private BukkitTask task;
    private AbstractWave wave;
    private int currentWave = 1;
    @Getter
    private boolean electricity;

    @Inject
    public HauntedIngamePhase(GameContext context) {
        super(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        startedSince = System.currentTimeMillis();
        startGameLoop();
        Bukkit.getScheduler().runTaskAsynchronously(context().plugin(), () -> {
            context().<HauntedMap>currentMap().sectionGates().forEach(SectionGate::spawnHologram);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        task.cancel();
    }

    public HauntedPlayer deadBodyInRange(Location location, double distance) {
        for (HauntedPlayer player : context().<HauntedPlayer>players()) {
            if (player.revivable() && player.deathLocation().distance(location) <= distance) {
                return player;
            }
        }
        return null;
    }

    public void initWave(AbstractWave wave) {
        if (this.wave != null) {
            this.wave.disable();
        }
        this.wave = wave;
        wave.enable();
        Message.broadcast(context().players(), context().messageConfiguration(), "Messages.waveBroadcast", "§7Welle §6%wave% §7beginnt!", "%wave%", Integer.toString(wave.wave()));
    }

    public void electricity(HauntedPlayer player, boolean electricity) {
        this.electricity = electricity;
        if (electricity) {
            Message.broadcast(context().players(), context().messageConfiguration(), "Messages.electricityOn", "§6%player% §7hat die §bElektrizität §7aktiviert.", "%player%", player.getName());
            for (SectionGate gate : context().<HauntedMap>currentMap().sectionGates()) {
                gate.electricity();
            }
        }
    }

    private void startGameLoop() {
        context().<HauntedPlayer>players().forEach(player -> {
            player.scoreboard().format("%time%", h -> DurationFormatUtils.formatDuration(System.currentTimeMillis() - startedSince, "mm:ss", true));
            player.getInventory().setHeldItemSlot(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 155, false, false));
        });
        task = Bukkit.getScheduler().runTaskTimer(context().plugin(), () -> {
            context().<HauntedPlayer>players().forEach(HauntedPlayer::update);
            if (wave != null && wave.done()) {
                currentWave ++;
                AbstractWave wave = context().<HauntedMap>currentMap().wave(currentWave);
                if (wave == null) {
                    endGame(context().players());
                } else {
                    initWave(wave);
                }
            }
        }, 20, 20);
        if (!OptionComponent.value(HauntedOptions.DEBUG_MODE)) {
            HauntedMap map = context().currentMap();
            AbstractWave wave = map.wave(1);
            if (wave == null) {
                Bukkit.getLogger().warning("Unable to begin with wave 1: No such wave");
                return;
            }
            initWave(wave);
        }
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
