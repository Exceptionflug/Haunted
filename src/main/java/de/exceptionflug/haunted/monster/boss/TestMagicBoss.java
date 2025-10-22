package de.exceptionflug.haunted.monster.boss;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monster.BossMonster;
import de.exceptionflug.projectvenom.game.GameContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class TestMagicBoss extends BossMonster {

    private final GameContext context;
    private final BossBar bossBar = Bukkit.createBossBar("TestMagicBoss", BarColor.PINK, BarStyle.SOLID, BarFlag.DARKEN_SKY);
    private Evoker evoker;

    @Inject
    public TestMagicBoss(GameContext context) {
        this.context = context;
    }

    @Override
    public List<Entity> allies() {
        return null;
    }

    @Override
    public void spawn(Location location) {
        evoker = (Evoker) location.getWorld().spawnEntity(location, EntityType.EVOKER);
        evoker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 99999, 20, false, false));
        context.players().forEach(player -> {
            bossBar.addPlayer(Objects.requireNonNull(player.handle()));
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                bossBar.setProgress(evoker.getHealth() / 24D);
                if (evoker.isDead()) {
                    cancel();
                    bossBar.removeAll();
                    context.players().forEach(player -> player.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1));
                    context.players().forEach(player -> player.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 0));
                }
            }
        }.runTaskTimer(context.plugin(), 5, 5);
    }

    @Override
    public void despawn() {
        bossBar.removeAll();
        evoker.remove();
    }

    @Override
    public boolean alive() {
        return !evoker.isDead();
    }

}
