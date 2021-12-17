package de.exceptionflug.haunted.wave;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.projectvenom.game.GameContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class StaticWave extends AbstractWave {

    private final Map<UUID, Monster> monsters;

    public StaticWave(GameContext context, int wave, Map<UUID, Monster> monsters) {
        super(context, wave);
        this.monsters = monsters;
    }

    @Override
    public void enable() {
        List<MobGate> mobGates = optimalSpawnGates();
        for (Monster monster : monsters.values()) {
            MobGate gate = mobGates.get(ThreadLocalRandom.current().nextInt(0, mobGates.size()));
            monster.spawn(gate.spawnLocation());
            monster.moveTo(gate.getFirstGateBlock());
        }
    }

    @Override
    public void disable() {
        monsters.values().forEach(Monster::despawn);
    }

    @Override
    public int remainingMonsters() {
        return (int) monsters.values().stream().filter(Monster::alive).count();
    }

    @Override
    public Map<UUID, Monster> entities() {
        return Map.copyOf(monsters);
    }

    @Override
    public boolean done() {
        return remainingMonsters() == 0;
    }
}
