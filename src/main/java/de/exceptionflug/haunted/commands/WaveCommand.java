package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.wave.AbstractWave;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.aop.command.VenomPaperCommand;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

/**
 * Date: 15.08.2021
 *
 * @author Exceptionflug
 */
@Singleton
public class WaveCommand extends VenomPaperCommand {

    @Inject
    public WaveCommand(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("wave")
                .requires(sender -> sender.getSender() instanceof Player
                        && sender.getSender().hasPermission("haunted.debug")
                        && context.phase().ingamePhase()
                )
                .then(Commands.literal("reload")
                    .executes(this::reloadCommand))
                .then(Commands.argument("wave", integer(1))
                    .executes(this::waveCommand))
                .build();
    }

    private int reloadCommand(CommandContext<CommandSourceStack> commandContext) {
        context.<HauntedMap>currentMap().reloadWaves();
        return 1;
    }

    private int waveCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        int waveNumber = commandContext.getArgument("wave", Integer.class);
        AbstractWave wave = context.<HauntedMap>currentMap().wave(waveNumber);
        HauntedIngamePhase phase = context.phase();
        phase.initWave(wave);
        player.sendMessage("§aWelle §6"+waveNumber+"§a eingeleitet.");
        return 1;
    }

}
