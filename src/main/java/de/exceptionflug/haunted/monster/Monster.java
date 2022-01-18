package de.exceptionflug.haunted.monster;

import de.exceptionflug.haunted.HauntedGameMode;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.player.GamePlayer;
import lombok.Getter;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public abstract class Monster {

    protected static GameContext getContext() {
        return HauntedGameMode.getGameContext();
    }

    protected static HauntedIngamePhase getPhase() {
        return getContext().phase();
    }

    @Getter
    private LivingEntity entity;

    @Getter
    private final Map<String, Object> attributes = new HashMap<>();

    public UUID getUUID() {
        return entity.getUniqueId();
    }

    public abstract void spawn(Location location);

    public void spawn(LivingEntity entity, Location location) {
        this.entity = entity;
        updateTarget();
    }

    public void despawn() {
        this.entity.remove();
        handleDeath();
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

    protected boolean canBreakGate = false;
    public boolean canBreakGate() {
        return canBreakGate;
    }

    protected NearestAttackableTargetGoal<net.minecraft.world.entity.player.Player> getPlayerGoal() {
        return new NearestAttackableTargetGoal<>(getNmsMob(), net.minecraft.world.entity.player.Player.class, false, p -> !HauntedGameMode.getGameContext().player(p.getUUID()).isDead());
    }

    public void updateTarget() {
        updateTarget(false);
    }

    public void updateTarget(boolean force) {
        Mob mob = getMob();
        if (mob == null) return;
        Player target = force ? getNearestPlayer(p -> mob.getTarget() == null || p.getUniqueId().equals(mob.getTarget().getUniqueId())) : getNearestPlayer();
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
        return getNearestPlayer(p -> true);
    }

    public Player getNearestPlayer(Predicate<Player> predicate) {
        List<Player> players = getAlivePlayers();
        if (players.size() == 1) return players.get(0);
        Location loc = entity.getLocation();
        double distance = 0;
        Player player = null;
        for (Player p : players) {
            if (!predicate.test(p)) continue;
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
        getNmsMob().getNavigation().moveTo(location.getX(), location.getY(), location.getZ(), 1D);
    }

    public void spawnParticle(Particle particle, int count) {
        Location location = getEntity().getLocation();
        getEntity().getWorld().spawnParticle(particle, location, count);
    }

    public void handleDeath() {};

    protected void despawnAllLinkedEntities() {
        Entity entity = getEntity();
        entity.getPassengers().forEach(Entity::remove);
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
            entity.remove();
        }
    }

    @Getter
    private long lastSuccessfulInteraction = System.currentTimeMillis();

    public void successfulInteraction() {
        lastSuccessfulInteraction = System.currentTimeMillis();
    }

    public void setGlowing(boolean value) {
        getEntity().setGlowing(value);
    }
}
