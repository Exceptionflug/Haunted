package de.exceptionflug.haunted.wave.config;

import de.exceptionflug.haunted.DebugUtil;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.SectionGate;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.shop.Shop;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import de.exceptionflug.regisseur.Cutscene;
import de.exceptionflug.regisseur.path.Position;
import de.exceptionflug.regisseur.path.Vector3D;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
public class InstructionExecutor {

    private final Map<String, Structure> declaredVariables = new HashMap<>();
    private final Map<String, Object> definedVariables = new HashMap<>();
    private final Map<String, Integer> globalInts = new HashMap<>();
    private final ConfiguredWave wave;
    private boolean cancelled;

    public InstructionExecutor(ConfiguredWave wave) {
        this.wave = wave;
    }

    @SneakyThrows
    public void execute(List<Statement> statements) {
        for (Statement statement : statements) {
            if (cancelled) {
                return;
            }
            executeStatement(preprocess(statement));
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public void addGlobalInt(String name, int value) {
        globalInts.put(name, value);
    }

    private Statement preprocess(Statement statement) throws Exception {
        Object[] arguments = new Object[statement.arguments().length];
        for (int i = 0; i < arguments.length; i++) {
            Object arg = statement.arguments()[i];
            if (arg instanceof WaveConfigurationParser.Variable v) {
                arguments[i] = definedVariables.get(v.name());
                if (arguments[i] == null) {
                    throw new RuntimeException("Variable "+v.name()+" is not defined yet");
                }
            } else if (arg instanceof Statement st) {
                arguments[i] = executeStatement(preprocess(st));
            } else {
                arguments[i] = statement.arguments()[i];
            }
        }
        return new Statement(statement.type(), arguments);
    }

    private Object executeStatement(Statement statement) throws Exception {
        return switch (statement.type()) {
            case WAIT -> executeWait(statement);
            case LOOP -> executeLoop(statement);
            case TELEPORT -> executeTeleport(statement);
            case SPAWN -> executeSpawn(statement);
            case CINEMATIC -> executeCinematic(statement);
            case SOUND -> executeSound(statement);
            case DECLARE -> executeDeclare(statement);
            case DEFINE -> executeDefine(statement);
            case RANDOM_INT -> executeRandomInt(statement);
            case RANDOM_DOUBLE -> executeRandomDouble(statement);
            case END -> executeEnd(statement);
            case ADD -> executeAdd(statement);
            case SUBTRACT -> executeSubtract(statement);
            case MULTIPLY -> executeMultiply(statement);
            case DIVIDE -> executeDivide(statement);
            case MODULO -> executeModulo(statement);
            case IF -> executeIf(statement);
            case COMPARE -> executeCompare(statement);
            case NEGATE -> executeNegate(statement);
            case AND -> executeAnd(statement);
            case OR -> executeOr(statement);
            case CONCAT -> executeConcat(statement);
            case LOAD_INT -> executeLoadInt(statement);
            case ECHO -> executeEcho(statement);
            default -> null;
        };
    }

    private int executeLoadInt(Statement statement) {
        return globalInts.get((String) statement.arguments()[0]);
    }

    private String executeConcat(Statement statement) {
        return String.valueOf(statement.arguments()[0]) + String.valueOf(statement.arguments()[1]);
    }

    private boolean executeOr(Statement statement) {
        return (boolean) statement.arguments()[0] || (boolean) statement.arguments()[1];
    }

    private boolean executeAnd(Statement statement) {
        return (boolean) statement.arguments()[0] && (boolean) statement.arguments()[1];
    }

    private boolean executeNegate(Statement statement) {
        return !((boolean) statement.arguments()[0]);
    }

    private boolean executeCompare(Statement statement) {
        return statement.arguments()[0].equals(statement.arguments()[1]);
    }

    private Void executeIf(Statement statement) {
        if ((boolean) statement.arguments()[0]) {
            execute(((WaveConfigurationParser.CodeBlock)statement.arguments()[1]).statements());
        }
        return null;
    }

    private Void executeModulo(Statement statement) {
        String argument = (String) statement.arguments()[0];
        ensureNumber(argument);
        Number number = (Number) definedVariables.get(argument);
        if (number instanceof Integer i) {
            definedVariables.put(argument, i % ((Number)statement.arguments()[1]).intValue());
        } else if (number instanceof Double d) {
            definedVariables.put(argument, d % ((Number)statement.arguments()[1]).doubleValue());
        }
        return null;
    }

    private Void executeDivide(Statement statement) {
        String argument = (String) statement.arguments()[0];
        ensureNumber(argument);
        Number number = (Number) definedVariables.get(argument);
        if (number instanceof Integer i) {
            definedVariables.put(argument, i / ((Number)statement.arguments()[1]).intValue());
        } else if (number instanceof Double d) {
            definedVariables.put(argument, d / ((Number)statement.arguments()[1]).doubleValue());
        }
        return null;
    }

    private Void executeMultiply(Statement statement) {
        String argument = (String) statement.arguments()[0];
        ensureNumber(argument);
        Number number = (Number) definedVariables.get(argument);
        if (number instanceof Integer i) {
            definedVariables.put(argument, i * ((Number)statement.arguments()[1]).intValue());
        } else if (number instanceof Double d) {
            definedVariables.put(argument, d * ((Number)statement.arguments()[1]).doubleValue());
        }
        return null;
    }

    private Void executeAdd(Statement statement) {
        String argument = (String) statement.arguments()[0];
        ensureNumber(argument);
        Number number = (Number) definedVariables.get(argument);
        if (number instanceof Integer i) {
            definedVariables.put(argument, i + ((Number)statement.arguments()[1]).intValue());
        } else if (number instanceof Double d) {
            definedVariables.put(argument, d + ((Number)statement.arguments()[1]).doubleValue());
        }
        return null;
    }

    private Void executeSubtract(Statement statement) {
        String argument = (String) statement.arguments()[0];
        ensureNumber(argument);
        Number number = (Number) definedVariables.get(argument);
        if (number instanceof Integer i) {
            definedVariables.put(argument, i - ((Number)statement.arguments()[1]).intValue());
        } else if (number instanceof Double d) {
            definedVariables.put(argument, d - ((Number)statement.arguments()[1]).doubleValue());
        }
        return null;
    }

    private void ensureNumber(String argument) {
        if (!declaredVariables.containsKey(argument)) {
            throw new RuntimeException("Unknown variable with name "+argument);
        }
        Object val = definedVariables.get(argument);
        if (!(val instanceof Number)) {
            throw new RuntimeException("Variable "+argument+" is not a type of number");
        }
    }

    private Void executeEcho(Statement statement) {
        DebugUtil.broadcastMessage(statement.arguments()[0].toString());
        return null;
    }

    private double executeRandomDouble(Statement statement) {
        return ThreadLocalRandom.current().nextDouble((double) statement.arguments()[0], (double) statement.arguments()[1]);
    }

    private int executeRandomInt(Statement statement) {
        return ThreadLocalRandom.current().nextInt((int) statement.arguments()[0], (int) statement.arguments()[1]);
    }

    private Void executeEnd(Statement statement) {
        wave.done(true);
        return null;
    }

    private Void executeDefine(Statement statement) {
        if (statement.arguments()[1] instanceof Statement st) {
            if (st.type().returnType() == null) {
                throw new RuntimeException("Statement has no return type");
            }
        } else {
            definedVariables.put((String) statement.arguments()[0], statement.arguments()[1]);
        }
        return null;
    }

    private Void executeDeclare(Statement statement) {
        declaredVariables.put((String) statement.arguments()[1], (Structure) statement.arguments()[0]);
        return null;
    }

    private Void executeSound(Statement statement) {
        float volume = ((Double) statement.arguments()[1]).floatValue();
        float pitch = ((Double) statement.arguments()[2]).floatValue();
        Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
            for (Player player : wave.context().players()) {
                player.playSound(player.getLocation(), (Sound) statement.arguments()[0], volume, pitch);
            }
        });
        return null;
    }

    private Void executeCinematic(Statement statement) {
        if (statement.arguments()[1] instanceof WaveConfigurationParser.CodeBlock block) {
            Cutscene cutscene = new Cutscene(wave.context().plugin());
            cutscene.players().addAll(wave.context().players());
            for (Statement insn : block.statements()) {
                if (insn.type() == Statement.InstructionType.VERTEX) {
                    Location location = (Location) insn.arguments()[0];
                    cutscene.addWaypoint(new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
                } else if (insn.type() == Statement.InstructionType.TARGET) {
                    Location location = (Location) insn.arguments()[0];
                    cutscene.target(new Vector3D(location.getX(), location.getY(), location.getZ()));
                }
            }
            Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
                for (SectionGate gate : wave.context().<HauntedMap>currentMap().sectionGates()) {
                    gate.despawn();
                }
                for (Shop shop : wave.context().<HauntedMap>currentMap().shops()) {
                    shop.despawn();
                }
                cutscene.startTravelling((int) statement.arguments()[0], wave.context().currentMap().spectatorSpawn().getWorld());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!cutscene.isTravelling()) {
                            cancel();
                            for (SectionGate gate : wave.context().<HauntedMap>currentMap().sectionGates()) {
                                gate.spawnHologram();
                            }
                            for (Shop shop : wave.context().<HauntedMap>currentMap().shops()) {
                                shop.spawn();
                            }
                        }
                    }
                }.runTaskTimer(wave.context().plugin(), 10, 10);
            });
        }
        return null;
    }

    private Void executeSpawn(Statement statement) {
        Class<?> clazz = (Class<?>) statement.arguments()[0];
        if (!Monster.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unable to spawn monster "+clazz.getName()+": class does not implement Monster");
        }
        Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
            wave.lock().lock();
            try {
                Monster monster = (Monster) wave.context().injector().getInstance(clazz);
                if (statement.arguments()[1] instanceof Location location) {
                    wave.monsters().add(monster);
                    monster.spawn(location);
                } else if (statement.arguments()[1] instanceof String string) {
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
        return null;
    }

    private Void executeTeleport(Statement statement) {
        Location location = (Location) statement.arguments()[0];
        Bukkit.getScheduler().runTask(wave.context().plugin(), () -> {
            for (Player player : wave.context().players()) {
                player.teleport(location);
            }
        });
        return null;
    }

    private Void executeLoop(Statement statement) {
        for (int i = 0; i < (int) statement.arguments()[0]; i++) {
            if (cancelled) {
                return null;
            }
            execute(((WaveConfigurationParser.CodeBlock)statement.arguments()[1]).statements());
        }
        return null;
    }

    private Void executeWait(Statement statement) throws InterruptedException {
        if (statement.arguments()[0] instanceof String string) {
            if (string.equals("CLEARED")) {
                Thread.sleep(500);
                while (wave.remainingMonsters() != 0) {
                    Thread.sleep(500);
                }
            } else {
                throw new RuntimeException("Unable to wait: Unknown specifier " + string);
            }
        } else {
            Thread.sleep((Integer) statement.arguments()[0]);
        }
        return null;
    }

}
