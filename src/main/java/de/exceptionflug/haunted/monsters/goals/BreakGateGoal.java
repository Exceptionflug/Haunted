package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.GateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class BreakGateGoal extends Goal {
    private final PathfinderMob mob;

    public BreakGateGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    public boolean canUse() {
        return this.mob.isOnGround();
    }

    private int breakTime = 0;
    private BlockPos blockPos;

    private int lastBreakProgress;
    private int blockBreakTime = 20;

    public void start() {
        this.breakTime = 0;
    }

    public void tick() {
        if (blockPos == null) {
            Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0D), Mth.floor(this.mob.getY() - 2.0D), Mth.floor(this.mob.getZ() - 2.0D), Mth.floor(this.mob.getX() + 2.0D), this.mob.getBlockY() + 1, Mth.floor(this.mob.getZ() + 2.0D));

            for (BlockPos pos : iterable) {
                if (isValidTarget(pos)) {
                    blockPos = pos;
                    GateUtils.lockGateBlock(blockPos, mob.getId());
                    break;
                }
            }
        }
        if (blockPos != null) {
            if (++breakTime > blockBreakTime) {
                // remove gate block
                GateUtils.breakGateBlock(blockPos);
                GateUtils.unlockGateBlock(blockPos);
                breakTime = 0;
                blockPos = null;
            } else {
                // break animation
                int i = (int)((float)breakTime / (float)blockBreakTime * 10.0F);
                if (i != lastBreakProgress) {
                    this.mob.level.destroyBlockProgress(this.mob.getId(), this.blockPos, i);
                    this.lastBreakProgress = i;
                }
            }
        }
    }

    @Override
    public void stop() {
        if (blockPos != null) GateUtils.unlockGateBlock(blockPos);
    }

    private boolean isValidTarget(BlockPos blockPos) {
        if (blockPos == null) return false;
        return GateUtils.isRepairedGateBlock(blockPos) && (!GateUtils.isGateBlockLocked(blockPos) || GateUtils.isGateBlockLockedBy(blockPos, mob.getId()));
    }
}