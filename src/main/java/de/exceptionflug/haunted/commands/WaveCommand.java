package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Component
@Command(value = "wave", inGameOnly = true, permission = "haunted.debug")
public class WaveCommand extends SpigotCommand {

    private final GameContext context;

    @Inject
    public WaveCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCommand(CommandInput commandInput) {
        if (commandInput.getString(0).equals("reload")) {
            context.<HauntedMap>currentMap().reloadWaves();
            return;
        }
        int waveNumber = commandInput.findInt(0, "waveCommandUsage", "§bBenutzung: §6/wave <int>");
        AbstractWave wave = context.<HauntedMap>currentMap().wave(waveNumber);
        HauntedIngamePhase phase = context.phase();
        phase.initWave(wave);
        getSender().getHandle().sendMessage("§aWelle §6"+waveNumber+"§a eingeleitet.");
    }

}
