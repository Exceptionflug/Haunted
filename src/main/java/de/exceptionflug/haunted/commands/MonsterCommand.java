package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.util.ClassScanUtil;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Singleton;
import de.exceptionflug.projectvenom.game.aop.command.VenomPaperCommand;
import de.exceptionflug.projectvenom.game.i18n.InternationalizationContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

@Singleton
public class MonsterCommand extends VenomPaperCommand {

    private final Set<Class<?>> monsterClasses;

    @Inject
    public MonsterCommand(GameContext context, InternationalizationContext i18nContext) {
        super(context, i18nContext);
        monsterClasses = ClassScanUtil.scanOwnArtifact(getClass(), "de.exceptionflug.haunted.monsters");
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> command() {
        return Commands.literal("monster")
                .requires(sender -> sender.getSender() instanceof Player
                        && sender.getSender().hasPermission("haunted.monster")
                        && context.phase().ingamePhase()
                )
                .then(Commands.literal("spawn")
                        .then(Commands.argument("monster", word())
                                .suggests(this::monsterSuggestions)
                                .executes(this::spawnCommand)))
                .build();
    }

    private int spawnCommand(CommandContext<CommandSourceStack> commandContext) {
        Player player = (Player) commandContext.getSource().getSender();
        String monsterType = commandContext.getArgument("monster", String.class);

        Class<?> clazz = null;
        try {
            clazz = Class.forName("de.exceptionflug.haunted.monsters." + monsterType);
        } catch (ClassNotFoundException ignored) {}
        try {
            if (clazz == null) clazz = Class.forName("de.exceptionflug.haunted.monsters." + monsterType + "Monster");
        } catch (ClassNotFoundException ignored) {}
        try {
            if (clazz == null) clazz = Class.forName(monsterType);
        } catch (ClassNotFoundException ignored) {}
        player.sendMessage("Monster class not found " + monsterType);
        if (!Monster.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unable to spawn monster "+clazz.getName()+": class does not implement Monster");
        }
        Monster monster = (Monster) context.injector().getInstance(clazz);
        monster.spawn(player.getLocation());
        player.sendMessage("Spawned " + clazz.getName());
        return 1;
    }

    private CompletableFuture<Suggestions> monsterSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder builder) {
        for (Class<?> monsterClass : monsterClasses) {
            builder.suggest(monsterClass.getName().replace("de.exceptionflug.haunted.monsters.", ""));
        }
        return builder.buildFuture();
    }
}
