package de.exceptionflug.haunted.wave;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.section.MapSection;
import de.exceptionflug.projectvenom.game.GameContext;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractWave {

    private final GameContext context;
    private final int wave;

    protected AbstractWave(GameContext context, int wave) {
        this.context = context;
        this.wave = wave;
    }

    public abstract void enable();
    public abstract void disable();

    public abstract int remainingMonsters();
    public abstract boolean done();

    protected List<MobGate> optimalSpawnGates() {
        List<MobGate> out = new ArrayList<>();
        for (MobGate gate : context.<HauntedMap>currentMap().mobGates()) {
            MapSection section = context.<HauntedMap>currentMap().mapSection(gate.mapSection());
            for (Player player : context.alivePlayers()) {
                if (section.isInside(player.getLocation())) {
                    out.add(gate);
                    break;
                }
            }
        }
        if (out.isEmpty()) {
            out.add(context.<HauntedMap>currentMap().mobGates().get(0));
        }
        return out;
    }

}
