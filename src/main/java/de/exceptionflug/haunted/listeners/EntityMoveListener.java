package de.exceptionflug.haunted.listeners;

import com.google.inject.Inject;
import de.exceptionflug.haunted.GateUtils;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import de.exceptionflug.projectvenom.game.phases.IngamePhase;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
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
        searchAndTargetGateBlock(event.getEntity());
    }

    private void searchAndTargetGateBlock(LivingEntity entity) {
        if (!(context.phase() instanceof IngamePhase)) return;
        if (set.contains(entity.getEntityId())) {
            //event.setCancelled(true);
        } else {
            Monster monster = context.<HauntedIngamePhase>phase().wave().monsterByEntity(entity);
            if (monster != null && monster.canBreakGate()) {
                Location loc = entity.getLocation();
                Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(loc.getX() - 2.0D), Mth.floor(loc.getY() - 2.0D), Mth.floor(loc.getZ() - 2.0D), Mth.floor(loc.getX() + 2.0D), loc.getBlockY() + 2, Mth.floor(loc.getZ() + 2.0D));

                for (BlockPos pos : iterable) {
                    if (pos.getY() == loc.getBlockY()+2 && !GateUtils.isGateBlock(pos.getX(), pos.getY()-1, pos.getZ())) continue;
                    if (isValidTarget(pos)) {
                        set.add(entity.getEntityId());
                        new GateBreakRunnable(monster, pos);
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

        private final BukkitTask task;
        private final LivingEntity entity;
        private final Monster monster;
        private int breakTime = 0;
        private final BlockPos blockPos;

        private int lastBreakProgress = 0;
        private int blockBreakTime = 20; // some gate blocks might require more time to break

        public GateBreakRunnable(Monster monster, BlockPos pos) {
            this.entity = monster.getEntity();
            this.monster = monster;
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
                monster.successfulInteraction();
                searchAndTargetGateBlock(entity);
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
