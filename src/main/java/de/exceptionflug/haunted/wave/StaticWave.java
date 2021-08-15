package de.exceptionflug.haunted.wave;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.projectvenom.game.GameContext;
import org.bukkit.Sound;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class StaticWave extends AbstractWave {

    private final List<Monster> monsters;

    public StaticWave(GameContext context, int wave, List<Monster> monsters) {
        super(context, wave);
        this.monsters = monsters;
    }

    @Override
    public void enable() {
        List<MobGate> mobGates = optimalSpawnGates();
        for (Monster monster : monsters) {
            monster.spawn(mobGates.get(ThreadLocalRandom.current().nextInt(0, mobGates.size())).spawnLocation());
        }
    }

    @Override
    public void disable() {
        monsters.forEach(Monster::despawn);
    }

    @Override
    public int remainingMonsters() {
        return (int) monsters.stream().filter(Monster::alive).count();
    }

    @Override
    public boolean done() {
        return remainingMonsters() == 0;
    }
}
