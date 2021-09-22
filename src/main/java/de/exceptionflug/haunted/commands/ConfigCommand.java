package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

/**
 * Date: 22.09.2021
 *
 * @author Exceptionflug
 */
@Component
@Command(value = "config", inGameOnly = true, permission = "haunted.config")
public class ConfigCommand extends SpigotCommand {

    private final GameContext context;

    @Inject
    public ConfigCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCommand(CommandInput commandInput) {

    }

    @SubCommand("block")
    public void block(CommandInput commandInput) {
        String path = commandInput.findString(0, "Messages.noConfigPath", "§cBitte gib einen Path an.");
        Player player = (Player) getSender().getHandle();
        context.<HauntedMap>currentMap().config().set(path, player.getLocation().getBlock().getLocation());
        context.<HauntedMap>currentMap().config().save();
        player.sendMessage("§aJo hat funktioniert.");
    }

    @SubCommand("loc")
    public void loc(CommandInput commandInput) {
        String path = commandInput.findString(0, "Messages.noConfigPath", "§cBitte gib einen Path an.");
        Player player = (Player) getSender().getHandle();
        context.<HauntedMap>currentMap().config().set(path, player.getLocation());
        context.<HauntedMap>currentMap().config().save();
        player.sendMessage("§aJo hat funktioniert.");
    }

    @SubCommand("look")
    public void look(CommandInput commandInput) {
        String path = commandInput.findString(0, "Messages.noConfigPath", "§cBitte gib einen Path an.");
        Player player = (Player) getSender().getHandle();
        RayTraceResult rayTraceResult = player.rayTraceBlocks(5D);
        if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
            player.sendMessage("§cGuck halt Block an.");
            return;
        }
        Block hitBlock = rayTraceResult.getHitBlock();
        context.<HauntedMap>currentMap().config().set(path, hitBlock.getLocation());
        context.<HauntedMap>currentMap().config().save();
        player.sendMessage("§aJo hat funktioniert.");
    }

}
