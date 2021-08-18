package de.exceptionflug.haunted.wave;

import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.wave.config.Statement;
import de.exceptionflug.haunted.wave.config.InstructionExecutor;
import de.exceptionflug.haunted.wave.config.ParseException;
import de.exceptionflug.haunted.wave.config.WaveConfigurationParser;
import de.exceptionflug.projectvenom.game.GameContext;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class ConfiguredWave extends AbstractWave {

    private final File file;
    private final List<Monster> monsters = new ArrayList<>();
    private final List<Statement> statements = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private InstructionExecutor instructionExecutor;
    private boolean done;

    public ConfiguredWave(File file, GameContext context) throws IOException, ParseException {
        super(context, parseEndReturnWaveNumber(file, context));
        this.file = file;
        parseInstructions();
    }

    private void parseInstructions() throws IOException, ParseException {
        WaveConfigurationParser parser = new WaveConfigurationParser(context(), file);
        Statement statement;
        while ((statement = parser.nextStatement()) != null) {
            statements.add(statement);
        }
        Bukkit.getLogger().info("Parsed "+ statements.size()+" main instructions from "+file.getName());
    }

    private static int parseEndReturnWaveNumber(File file, GameContext context) throws IOException, ParseException {
        WaveConfigurationParser parser = new WaveConfigurationParser(context, file);
        return parser.wave();
    }

    @Override
    public void enable() {
        Bukkit.getScheduler().runTaskAsynchronously(context().plugin(), () -> {
            instructionExecutor = new InstructionExecutor(this);
            instructionExecutor.addGlobalInt("alivePlayerCount", context().alivePlayers().size());
            instructionExecutor.execute(statements);
        });
    }

    @Override
    public void disable() {
        monsters.forEach(Monster::despawn);
        instructionExecutor.cancel();
    }

    @Override
    public int remainingMonsters() {
        lock.lock();
        try {
            return (int) monsters.stream().filter(Monster::alive).count();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean done() {
        return done;
    }

    public void done(boolean done) {
        this.done = done;
    }

    public void optimalSpawn(Monster monster) {
        List<MobGate> mobGates = optimalSpawnGates();
        monster.spawn(mobGates.get(ThreadLocalRandom.current().nextInt(0, mobGates.size())).spawnLocation());
    }

}
