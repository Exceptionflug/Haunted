package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.aop.command.VenomPaperCommand;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

/**
 * Date: 22.09.2021
 *
 * @author Exceptionflug
 */
@Singleton
public class ConfigCommand extends VenomPaperCommand {

    @Inject
    public ConfigCommand(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("config")
                .requires(sender -> sender.getSender() instanceof Player
                        && sender.getSender().hasPermission("haunted.config")
                        && context.phase().ingamePhase()
                )
                .then(Commands.literal("block")
                        .then(Commands.argument("path", string())
                        .executes(this::blockCommand)))
                .then(Commands.literal("loc")
                        .then(Commands.argument("path", string())
                                .executes(this::locCommand)))
                .then(Commands.literal("look")
                        .then(Commands.argument("path", string())
                                .executes(this::lookCommand)))
                .build();
    }

    private int blockCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        String path = commandContext.getArgument("path", String.class);
        context.<HauntedMap>currentMap().config().set(path, player.getLocation().getBlock().getLocation());
        player.sendMessage("§aJo hat funktioniert.");
        return 1;
    }

    private int locCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        String path = commandContext.getArgument("path", String.class);
        context.<HauntedMap>currentMap().config().set(path, player.getLocation());
        player.sendMessage("§aJo hat funktioniert.");
        return 1;
    }

    private int lookCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        String path = commandContext.getArgument("path", String.class);
        RayTraceResult rayTraceResult = player.rayTraceBlocks(5D);
        if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
            player.sendMessage("§cGuck halt Block an.");
            return 0;
        }
        Block hitBlock = rayTraceResult.getHitBlock();
        context.<HauntedMap>currentMap().config().set(path, hitBlock.getLocation());
        player.sendMessage("§aJo hat funktioniert.");
        return 1;
    }
}
