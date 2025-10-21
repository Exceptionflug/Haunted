package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.util.HighlightUtil;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.aop.command.VenomPaperCommand;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Date: 23.09.2021
 *
 * @author Exceptionflug
 */
@Singleton
public class DebugCommand extends VenomPaperCommand {

    @Inject
    public DebugCommand(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("hdebug")
                .requires(sender -> sender.getSender() instanceof Player
                        && sender.getSender().hasPermission("haunted.debug")
                        && context.phase().ingamePhase()
                )
                .then(Commands.literal("gates")
                        .executes(this::gatesCommand))
                .then(Commands.literal("zones")
                        .executes(this::zonesCommand))
                .build();
    }

    private int gatesCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        HauntedMap map = context.currentMap();
        map.sectionGates().forEach(sectionGate -> HighlightUtil.highlight(sectionGate.region(), ChatColor.BLUE));
        map.mobGates().forEach(MobGate::debug);
        Bukkit.getScheduler().runTaskAsynchronously(context.plugin(), () -> {
            for (MobGate mobGate : map.mobGates()) {
                mobGate.asyncDebug();
            }
        });
        player.sendMessage("Debug an");
        return 1;
    }

    private int zonesCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        HauntedMap map = context.currentMap();
        map.sections().forEach(section -> {
            ChatColor random = ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)];
            while (random.isFormat()) {
                random = ChatColor.values()[ThreadLocalRandom.current().nextInt(ChatColor.values().length)];
            }
            ChatColor finalRandom = random;
            player.sendMessage(finalRandom + section.displayName());
            section.regions().forEach(region -> {
                HighlightUtil.highlight(region, finalRandom);
            });
        });
        player.sendMessage("Debug an");
        return 1;
    }

}
