package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.GateUtils;
import de.exceptionflug.haunted.monster.GateMonster;
import de.exceptionflug.haunted.wave.AbstractWave;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import org.bukkit.Bukkit;

public class AttackGateGoal extends MoveToBlockGoal {

    private final AbstractWave wave;

    public AttackGateGoal(PathfinderMob entitycreature, double speed, int blockBreakTime, AbstractWave wave) {
        super(entitycreature, speed, 8, 2);
        this.wave = wave;
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
            this.nextStartTick = 20;
            return true;
        } else {
            this.nextStartTick = this.nextStartTick(this.mob);
            return false;
        }
    }

    private int tryFindBlockFails = 0;
    private void failedTryFindBlock() {
        if (++tryFindBlockFails > 1) {
            if (wave.monsterByEntityId(mob.getId()) instanceof GateMonster monster) {
                Bukkit.getScheduler().runTask(wave.context().plugin(), monster::removeAttackGateGoal);
            }
        }
    }

    private boolean tryFindBlock() {
        if (this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos)) {
            return true;
        } else if (this.findNearestBlock()) {
            this.blockPos = GateUtils.getDamageableGateBlock(this.blockPos);
            return true;
        }
        failedTryFindBlock();
        return false;
    }

    public boolean findNearestBlock() {
        if (GateUtils.isGateBlockLockedBy(blockPos, mob.getId())) GateUtils.unlockGateBlock(blockPos);
        return super.findNearestBlock();
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
        if ((isReachedTarget() || breakTime > 0) && this.blockPos != null) {
            if (breakTime % randomHitInterval == 0) {
                playDestroyProgressSound(this.mob.level, blockPos);
                if (!this.mob.swinging) {
                    this.mob.getLookControl().setLookAt(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
                    this.mob.swing(this.mob.getUsedItemHand());
                }
            }

            if (breakTime == 0 && !GateUtils.isGateBlockLocked(blockPos)) {
                GateUtils.lockGateBlock(blockPos, mob.getId());
                nextStartTick = blockBreakTime + 20;
            }


            ++this.breakTime;
            int i = (int)((float)this.breakTime / (float)blockBreakTime * 10.0F);
            if (i != this.lastBreakProgress) {
                this.mob.level.destroyBlockProgress(this.mob.getId(), this.blockPos, i);
                this.lastBreakProgress = i;
            }

            if (this.breakTime == this.blockBreakTime) {
                GateUtils.breakGateBlock(this.blockPos);
                GateUtils.unlockGateBlock(blockPos);
            }
        }
    }

    protected BlockPos getMoveToTarget() {
        if (GateUtils.isGateBlock(this.blockPos.down())) return this.blockPos.down();
        return this.blockPos;
    }

    protected boolean isValidTarget(LevelReader iworldreader, BlockPos blockposition) {
        if (blockposition == null) return false;
        return GateUtils.isRepairedGateBlock(blockposition) && (!GateUtils.isGateBlockLocked(blockposition) || GateUtils.isGateBlockLockedBy(blockposition, mob.getId()));
    }

    public double acceptedDistance() {
        return 2.5D;
    }
}
