package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.GateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;

public class AttackGateGoal extends MoveToBlockGoal {

    public AttackGateGoal(PathfinderMob entitycreature, double speed, int blockBreakTime) {
        super(entitycreature, speed, 24, 3);
        this.blockBreakTime = blockBreakTime;
    }

    public void playDestroyProgressSound(LevelAccessor generatoraccess, BlockPos blockposition) {
        generatoraccess.playSound((Player)null, blockposition, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 0.5F, 0.9F);
    }

    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        } else if (this.tryFindBlock()) {
            this.nextStartTick = 5;
            return true;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return false;
        }
    }

    private boolean tryFindBlock() {
        return this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos) || this.findNearestBlock();
    }

    protected int breakTime;
    protected int lastBreakProgress;
    protected int blockBreakTime;
    private int randomHitInterval = Math.max(5, blockBreakTime/5);

    @Override
    public void start() {
        super.start();
        this.breakTime = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (isReachedTarget() && this.blockPos != null) {
            if (this.mob.level.getBlockState(blockPos).isAir()) {
                blockPos = null;
                super.stop();
                return;
            }

            if (this.mob.getRandom().nextInt(randomHitInterval) == 0) {
                // block hit sound (zombie attack wooden door)
                //this.mob.level.levelEvent(1019, this.blockPos, 0);
                playDestroyProgressSound(this.mob.level, blockPos);
                if (!this.mob.swinging) {
                    this.mob.getLookControl().setLookAt(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
                    this.mob.swing(this.mob.getUsedItemHand());
                }
            }

            ++this.breakTime;
            int i = (int)((float)this.breakTime / (float)blockBreakTime * 10.0F);
            if (i != this.lastBreakProgress) {
                this.mob.level.destroyBlockProgress(this.mob.getId(), this.blockPos, i);
                this.lastBreakProgress = i;
            }

            if (this.breakTime == this.blockBreakTime) {
                GateUtils.breakGateBlock(this.blockPos);
                //this.mob.level.removeBlock(this.blockPos, false);
                //this.mob.level.levelEvent(1021, this.blockPos, 0);
                //block break sound and particle?
                //this.mob.level.levelEvent(2001, this.blockPos, Block.getId(this.mob.level.getBlockState(this.blockPos)));
            }
        }
    }

    protected BlockPos getMoveToTarget() {
        if (GateUtils.isGateBlock(this.blockPos.down())) return this.blockPos.down();
        return this.blockPos;
    }

    protected boolean isValidTarget(LevelReader iworldreader, BlockPos blockposition) {
        return GateUtils.isRepairedGateBlock(blockposition) && !GateUtils.isGateBlockLocked(blockposition);
    }

    public double acceptedDistance() {
        return 3D;
    }
}
