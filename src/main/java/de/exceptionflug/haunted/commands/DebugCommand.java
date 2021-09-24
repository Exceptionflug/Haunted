package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.gate.MobGate;
import de.exceptionflug.haunted.util.HighlightUtil;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Date: 23.09.2021
 *
 * @author Exceptionflug
 */
@Component
@Command(value = "hdebug", permission = "haunted.debug", inGameOnly = true)
public class DebugCommand extends SpigotCommand {

    private final GameContext context;

    @Inject
    public DebugCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCommand(CommandInput commandInput) {

    }

    @SubCommand("gates")
    public void gates(CommandInput input) {
        Player player = (Player) getSender().getHandle();
        HauntedMap map = context.currentMap();
        map.sectionGates().forEach(sectionGate -> HighlightUtil.highlight(sectionGate.region(), ChatColor.BLUE));
        map.mobGates().forEach(MobGate::debug);
        Bukkit.getScheduler().runTaskAsynchronously(context.plugin(), () -> {
           for (MobGate mobGate : map.mobGates()) {
               mobGate.asyncDebug();
           }
        });
        player.sendMessage("Debug an");
    }

    @SubCommand("zones")
    public void zones(CommandInput input) {
        Player player = (Player) getSender().getHandle();
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
    }

}
