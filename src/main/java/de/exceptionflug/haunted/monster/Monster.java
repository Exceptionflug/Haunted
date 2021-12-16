package de.exceptionflug.haunted.monster;

import de.exceptionflug.haunted.HauntedGameMode;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public abstract class Monster {

    private static GameContext context = HauntedGameMode.getGameContext();

    @Getter
    private LivingEntity entity;

    public abstract void spawn(Location location);

    public void spawn(LivingEntity entity, Location location) {
        this.entity = entity;
        updateTarget();
    }

    public void despawn() {
        this.entity.remove();
    }

    public boolean alive() {
        return !entity.isDead();
    }

    public net.minecraft.world.entity.Entity getNmsEntity() {
        return ((CraftEntity) entity).getHandle();
    }

    public boolean isMob() {
        return entity instanceof Mob;
    }

    public Mob getMob() {
        if (entity instanceof Mob mob) return mob;
        return null;
    }

    public net.minecraft.world.entity.Mob getNmsMob() {
        if (getNmsEntity() instanceof net.minecraft.world.entity.Mob mob) return mob;
        return null;
    }

    public boolean canBreakGate() {
        return false;
    }

    protected Goal getPlayerGoal() {
        return new NearestAttackableTargetGoal<>(getNmsMob(), net.minecraft.world.entity.player.Player.class, false, p -> !context.player(p.getUUID()).isDead());
    }

    public void updateTarget() {
        Mob mob = getMob();
        if (mob == null) return;
        Player target = getNearestPlayer();
        if (mob.getTarget() == null) {
            if (target != null) mob.setTarget(target);
        } else {
            if (!mob.getTarget().equals(target)) mob.setTarget(target);
        }
    }

    private List<Player> getAlivePlayers() {
        return HauntedGameMode.getGameContext().alivePlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
    }

    public Player getNearestPlayer() {
        List<Player> players = getAlivePlayers();
        if (players.size() == 1) return players.get(0);
        Location loc = entity.getLocation();
        double distance = 0;
        Player player = null;
        for (Player p : players) {
            double d = loc.distanceSquared(p.getLocation());
            if (distance < d) {
                distance = d;
                player = p;
            }
        }
        return player;
    }

    public void setMaxHealth(int health) {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    public void moveTo(Location location) {
        getNmsEntity().moveTo(location.getX(), location.getY(), location.getZ());
    }

    public void spawnParticle(Particle particle, int count) {
        Location location = getEntity().getLocation();
        getEntity().getWorld().spawnParticle(particle, location, count);
    }
}
