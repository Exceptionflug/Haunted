package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import net.minecraft.world.entity.LivingEntity;

public abstract class ThrowerMonster extends Monster {
    public abstract void performRangedAttack(LivingEntity target, float var4);
}
