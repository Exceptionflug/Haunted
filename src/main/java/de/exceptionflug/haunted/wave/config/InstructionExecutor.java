package de.exceptionflug.haunted.wave.config;

import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class InstructionExecutor {

    private final ConfiguredWave wave;

    public InstructionExecutor(ConfiguredWave wave) {
        this.wave = wave;
    }

    @SneakyThrows
    public void execute(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            if (instruction.type() == Instruction.InstructionType.WAIT) {
                if (instruction.arguments()[0] instanceof String string) {
                    if (string.equals("CLEARED")) {
                        while (wave.remainingMonsters() != 0) {
                            Thread.sleep(500);
                        }
                    } else {
                        throw new RuntimeException("Unable to wait: Unknown specifier " + string);
                    }
                } else {
                    Thread.sleep((Integer) instruction.arguments()[0]);
                }
            } else if (instruction.type() == Instruction.InstructionType.LOOP) {
                for (int i = 0; i < (int) instruction.arguments()[0]; i++) {
                    execute(((WaveConfigurationParser.CodeBlock)instruction.arguments()[1]).instructions());
                }
            } else if (instruction.type() == Instruction.InstructionType.TELEPORT) {
                Location location = (Location) instruction.arguments()[0];
                Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
                    for (Player player : wave.context().players()) {
                        player.teleport(location);
                    }
                });
            } else if (instruction.type() == Instruction.InstructionType.SPAWN) {
                Class<?> clazz = (Class<?>) instruction.arguments()[0];
                if (!Monster.class.isAssignableFrom(clazz)) {
                    throw new RuntimeException("Unable to spawn monster "+clazz.getName()+": class does not implement Monster");
                }
                Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
                    wave.lock().lock();
                    try {
                        Monster monster = (Monster) wave.context().injector().getInstance(clazz);
                        if (instruction.arguments()[1] instanceof Location location) {
                            wave.monsters().add(monster);
                            monster.spawn(location);
                        } else if (instruction.arguments()[1] instanceof String string) {
                            if (string.equals("AUTOMATIC")) {
                                wave.monsters().add(monster);
                                wave.optimalSpawn(monster);
                            } else {
                                throw new RuntimeException("Unable to spawn monster "+clazz.getName()+": Unknown specifier " + string);
                            }
                        }
                    } finally {
                        wave.lock().unlock();
                    }
                });
            } else if (instruction.type() == Instruction.InstructionType.CINEMATIC) {

            } else if (instruction.type() == Instruction.InstructionType.SOUND) {
                float volume = ((Double) instruction.arguments()[1]).floatValue();
                float pitch = ((Double) instruction.arguments()[2]).floatValue();
                Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
                    for (Player player : wave.context().players()) {
                        player.playSound(player.getLocation(), (Sound) instruction.arguments()[0], volume, pitch);
                    }
                });
            } else if (instruction.type() == Instruction.InstructionType.END) {
                wave.done(true);
            }
        }
    }

}
