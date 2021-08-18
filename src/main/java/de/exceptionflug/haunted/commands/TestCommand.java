package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.entity.Player;

/**
 * Date: 17.08.2021
 *
 * @author Exceptionflug
 */
@Component
@Command("corpse")
public class TestCommand extends SpigotCommand {

    private final GameContext gameContext;

    @Inject
    public TestCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void onCommand(CommandInput commandInput) {
        try {
            Player player = (Player) getSender().getHandle();
            HauntedPlayer hauntedPlayer = gameContext.player(player);
            hauntedPlayer.createLayingBody(player.getLocation());
            player.sendMessage("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
