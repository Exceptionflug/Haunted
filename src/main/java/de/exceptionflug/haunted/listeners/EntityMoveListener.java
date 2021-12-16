package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.GateUtils;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

@Component
public class EntityMoveListener implements Listener {

    private final GameContext context;

    @Inject
    public EntityMoveListener(GameContext gameContext) {
        this.context = gameContext;
    }

    private Set<Integer> set = new HashSet<>();

    @EventHandler
    public void onMove(EntityMoveEvent event) {
        // if changed block
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;
        // if is gate monster
        if (set.contains(event.getEntity().getEntityId())) {
            //event.setCancelled(true);
        } else {
            Monster monster = context.<HauntedIngamePhase>phase().wave().monsterByEntity(event.getEntity());
            if (monster != null && monster.canBreakGate()) {
                Location loc = event.getTo();
                Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(loc.getX() - 2.0D), Mth.floor(loc.getY() - 2.0D), Mth.floor(loc.getZ() - 2.0D), Mth.floor(loc.getX() + 2.0D), loc.getBlockY() + 1, Mth.floor(loc.getZ() + 2.0D));

                for (BlockPos pos : iterable) {
                    if (isValidTarget(pos)) {
                        set.add(event.getEntity().getEntityId());
                        new GateBreakRunnable(event.getEntity(), pos);
                        monster.getNmsEntity().moveTo(pos.getX(), pos.getY(), pos.getZ());
                        break;
                    }
                }
            }
        }
    }

    private boolean isValidTarget(BlockPos blockPos) {
        if (blockPos == null) return false;
        return GateUtils.isRepairedGateBlock(blockPos) && !GateUtils.isGateBlockLocked(blockPos);
    }

    private class GateBreakRunnable implements Runnable {

        private BukkitTask task;

        private Entity entity;
        private int breakTime = 0;
        private BlockPos blockPos;

        private int lastBreakProgress = 0;
        private int blockBreakTime = 20;

        public GateBreakRunnable(LivingEntity entity, BlockPos pos) {
            this.entity = entity;
            this.blockPos = pos;
            GateUtils.lockGateBlock(blockPos, entity.getEntityId());
            this.task = Bukkit.getScheduler().runTaskTimer(context.plugin(), this, 0, 1);
        }

        @Override
        public void run() {
            if (entity.isDead()) {
                ((CraftWorld) entity.getWorld()).getHandle().destroyBlockProgress(entity.getEntityId(), this.blockPos, 0);
                GateUtils.unlockGateBlock(blockPos);
                set.remove(entity.getEntityId());
                stop();
            }
            if (++breakTime > blockBreakTime) {
                // remove gate block
                GateUtils.breakGateBlock(blockPos);
                GateUtils.unlockGateBlock(blockPos);
                set.remove(entity.getEntityId());
                stop();
            } else {
                // break animation
                int i = (int)((float)breakTime / (float)blockBreakTime * 10.0F);
                if (i != lastBreakProgress) {
                    ((CraftWorld) entity.getWorld()).getHandle().destroyBlockProgress(entity.getEntityId(), this.blockPos, i);
                    this.lastBreakProgress = i;
                }
            }
        }

        public void stop() {
            this.task.cancel();
        }
    }
}
