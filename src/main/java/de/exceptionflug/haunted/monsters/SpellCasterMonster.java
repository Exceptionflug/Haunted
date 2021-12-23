package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.GateMonster;
import de.exceptionflug.haunted.monsters.goals.PassiveSpellGoal;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class SpellCasterMonster extends GateMonster {

    @Getter
    @Setter
    private boolean castingSpell;

    @Override
    public void spawn(LivingEntity entity, Location location) {
        super.spawn(entity, location);
        if (getNmsEntity() instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(1, new PassiveSpellGoal(this));
        }
    }

    public abstract void performSpellCasting();
}
