package de.exceptionflug.haunted.monsters.goals;

import de.exceptionflug.haunted.monsters.SpellCasterMonster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PassiveSpellGoal extends Goal {

    private final Mob mob;
    private final SpellCasterMonster monster;

    private int nextSpellTickCount;
    private int spellWarmupDelay;

    public PassiveSpellGoal(SpellCasterMonster monster) {
        this.monster = monster;
        this.mob = monster.getNmsMob();
    }

    @Override
    public void start() {
        this.nextSpellTickCount = mob.tickCount + getCastingInterval();
        this.spellWarmupDelay = getCastWarmupTime();
        monster.setCastingSpell(true);
    }

    @Override
    public boolean canUse() {
        if (mob.getTarget() != null && mob.getTarget().isAlive()) {
            if (monster.isCastingSpell()) return false;
            return mob.tickCount >= nextSpellTickCount;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive() && spellWarmupDelay > 0;
    }

    @Override
    public void stop() {
        monster.setCastingSpell(false);
    }

    @Override
    public void tick() {
        if (--spellWarmupDelay == 0) monster.performSpellCasting();
    }

    private int getCastWarmupTime() {
        return 20;
    }

    private int getCastingTime() {
        return 40;
    }

    private int getCastingInterval() {
        return 140;
    }
}
