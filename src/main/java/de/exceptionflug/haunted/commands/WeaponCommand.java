package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.entity.Player;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
@Component
@Command(value = "weapon", inGameOnly = true, permission = "haunted.debug")
public class WeaponCommand extends SpigotCommand {

    private final GameContext context;

    @Inject
    public WeaponCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCommand(CommandInput commandInput) {
        Player player = (Player) getSender().getHandle();
        if (commandInput.getArgCount() == 0) {
            player.sendMessage("§bBenutzung: §6/weapon <Name>");
            return;
        }
        HauntedPlayer hauntedPlayer = context.player(player);
        try {
            Gun gun = new Gun(GunType.valueOf(commandInput.getString(0)), hauntedPlayer, context);
            hauntedPlayer.primaryWeapon(gun);
            player.sendMessage("§aWaffe erhalten.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUnbekannte Waffe.");
        }
    }

}
