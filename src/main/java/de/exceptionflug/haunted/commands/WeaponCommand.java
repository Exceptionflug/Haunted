package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.weapon.Gun;
import de.exceptionflug.haunted.weapon.GunType;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.aop.command.VenomPaperCommand;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * Date: 14.08.2021
 *
 * @author Exceptionflug
 */
@Singleton
public class WeaponCommand extends VenomPaperCommand {

    @Inject
    public WeaponCommand(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("weapon")
                .requires(sender -> sender.getSender() instanceof Player
                        && sender.getSender().hasPermission("haunted.debug")
                        && context.phase().ingamePhase()
                )
                .executes(this::usageCommand)
                .then(Commands.argument("weapon", word())
                        .suggests((ctx, builder) -> {
                            for (GunType value : GunType.values()) {
                                builder.suggest(value.toString());
                            }
                            return builder.buildFuture();
                        })
                        .executes(this::giveCommand))
                .build();
    }

    private int usageCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        player.sendMessage("§bBenutzung: §6/weapon <Name>");
        return 1;
    }

    private int giveCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        HauntedPlayer hauntedPlayer = context.player(player);
        try {
            Gun gun = new Gun(GunType.valueOf(commandContext.getArgument("weapon", String.class)), hauntedPlayer, context);
            hauntedPlayer.primaryWeapon(gun);
            player.sendMessage("§aWaffe erhalten.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUnbekannte Waffe.");
        }
        return 1;
    }

}
