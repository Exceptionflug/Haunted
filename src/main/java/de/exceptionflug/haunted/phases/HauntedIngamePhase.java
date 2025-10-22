package de.exceptionflug.haunted.phases;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import com.google.inject.Inject;
import de.exceptionflug.haunted.HauntedOptions;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import de.exceptionflug.projectvenom.game.option.OptionComponent;
import de.exceptionflug.projectvenom.game.phases.IngamePhase;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

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
    private long waveTime;

    @Inject
    public HauntedIngamePhase(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
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
        this.waveTime = System.currentTimeMillis();
        this.wave = wave;
        wave.enable();
        i18nContext().broadcast(context().players().stream().map(GamePlayer::handle).toList(), "Messages.waveBroadcast", c -> {
            c.setDefaultMessage(() -> "§7Welle §6%wave% §7beginnt!");
            c.setArgument("wave", Integer.toString(wave.wave()));
        });
    }

    public void electricity(HauntedPlayer player, boolean electricity) {
        this.electricity = electricity;
        if (electricity) {
            context().players().forEach(player1 -> player1.playSound(Sound.ENTITY_WITHER_DEATH, 5, 1));
            i18nContext().broadcast(context().players().stream().map(GamePlayer::handle).toList(), "Messages.electricityOn", c -> {
                c.setDefaultMessage(() -> "§6%player% §7hat die §bElektrizität §7aktiviert.");
                c.setArgument("player", player.handle().getName());
            });
            for (SectionGate gate : context().<HauntedMap>currentMap().sectionGates()) {
                gate.electricity();
            }
        }
    }

    private void startGameLoop() {
        context().<HauntedPlayer>players().forEach(player -> {
            updateScoreboard(player);
            player.handle().getInventory().setHeldItemSlot(0);
            player.handle().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 155, false, false));
        });
        task = Bukkit.getScheduler().runTaskTimer(context().plugin(), () -> {
            context().<HauntedPlayer>players().forEach(HauntedPlayer::update);
            if (!OptionComponent.value(HauntedOptions.DEBUG_MODE)) {
                if (wave != null) {
                    if (wave.done()) {
                        currentWave ++;
                        AbstractWave wave = context().<HauntedMap>currentMap().wave(currentWave);
                        if (wave == null) {
                            endGame(context().players());
                        } else {
                            for (HauntedPlayer player : context().<HauntedPlayer>players()) {
                                if (player.dead()) {
                                    player.respawn();
                                    player.handle().sendTitle("§aDu wurdest wiederbelebt!", "", 10, 40, 10);
                                }
                            }
                            initWave(wave);
                        }
                    } else {
                        if (System.currentTimeMillis() - waveTime > 120000) {
                            wave.entities().values().forEach(monster -> {
                                if (!monster.getEntity().isDead() && !monster.getEntity().isGlowing()) {
                                    monster.setGlowing(true);
                                }
                            });
                        }
                    }
                }
            }
        }, 20, 20);
        if (!OptionComponent.value(HauntedOptions.DEBUG_MODE)) {
            HauntedMap map = context().currentMap();
            AbstractWave wave = map.wave(1);
            if (wave == null) {
                context().plugin().getLogger().warning("Unable to begin with wave 1: No such wave");
                return;
            }
            initWave(wave);
        }
    }

    public void updateScoreboard(HauntedPlayer player) {
        SidebarComponent lines = SidebarComponent.builder()
                .addDynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.section", c -> {
                    c.setDefaultMessage(() -> "&7» &b%section%");
                    c.setArgument("section", player.currentSection());
                }))
                .addBlankLine()
                .addDynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.wave", c -> {
                    c.setDefaultMessage(() -> "&7Welle: &b%wave%");
                    String displayWave;
                    if (!context().phase().ingamePhase()) {
                        displayWave = "0";
                    } else {
                        displayWave = Integer.toString(context().<HauntedIngamePhase>phase().currentWave());
                    }
                    c.setArgument("wave", displayWave);
                }))
                .addDynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.remaining", c -> {
                    c.setDefaultMessage(() -> "&7Monster: &b%remaining%");
                    String displayRemaining;
                    if (!context().phase().ingamePhase()) {
                        displayRemaining = "0";
                    } else {
                        HauntedIngamePhase phase = context().phase();
                        if (phase.wave() != null) {
                            displayRemaining = Integer.toString(phase.wave().remainingMonsters());
                        } else {
                            displayRemaining = "0";
                        }
                    }
                    c.setArgument("remaining", displayRemaining);
                }))
                .addDynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.gold", c -> {
                    c.setDefaultMessage(() -> "&7Gold: &b%gold%");
                    c.setArgument("gold", Integer.toString(player.gold()));
                }))
                .addBlankLine()
                .addDynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.kills", c -> {
                    c.setDefaultMessage(() -> "&7Kills: &b%kills%");
                    c.setArgument("kills", Integer.toString(player.kills()));
                }))
                .addBlankLine()
                .build();

        ComponentSidebarLayout layout = new ComponentSidebarLayout(
                SidebarComponent.dynamicLine(() -> player.i18n().getMessage(player.handle(), "Scoreboard.title", c -> {
                    c.setDefaultMessage(() -> "&lHAUNTED &8| &7%time%");
                    c.setArgument("time", DurationFormatUtils.formatDuration(System.currentTimeMillis() - startedSince, "mm:ss", true));
                })),
                lines
        );
        layout.apply(player.scoreboard());
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
    public void onDismount(EntityDismountEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            HauntedPlayer player = context().player(event.getTarget().getUniqueId());
            if (player.spectator()) {
                event.setTarget(context().alivePlayers().get(ThreadLocalRandom.current().nextInt(context().alivePlayers().size())).handle());
            }
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
                updateScoreboard(player);
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

    public AbstractWave wave() {
        return wave;
    }

    public int currentWave() {
        return currentWave;
    }
}
