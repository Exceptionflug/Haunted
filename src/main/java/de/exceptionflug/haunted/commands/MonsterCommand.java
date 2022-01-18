package de.exceptionflug.haunted.commands;

import com.google.inject.Inject;
import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.mccommons.commands.api.annotation.Command;
import de.exceptionflug.mccommons.commands.api.annotation.SubCommand;
import de.exceptionflug.mccommons.commands.api.input.CommandInput;
import de.exceptionflug.mccommons.commands.spigot.command.SpigotCommand;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.aop.Component;
import org.bukkit.entity.Player;

@Component
@Command(value = "monster", permission = "haunted.monster", inGameOnly = true)
public class MonsterCommand extends SpigotCommand {

    private final GameContext context;

    @Inject
    public MonsterCommand(GameContext context) {
        this.context = context;
    }

    @Override
    public void onCommand(CommandInput commandInput) {

    }

    @SubCommand("spawn")
    public void spawn(CommandInput input) {
        Player player = (Player) getSender().getHandle();
        Class<?> clazz = null;
        String str = input.getString(0);
        try {
            clazz = Class.forName("de.exceptionflug.haunted.monsters." + str);
        } catch (ClassNotFoundException ignored) {}
        try {
            if (clazz == null) clazz = Class.forName("de.exceptionflug.haunted.monsters." + str + "Monster");
        } catch (ClassNotFoundException ignored) {}
        try {
            if (clazz == null) clazz = Class.forName(str);
        } catch (ClassNotFoundException ignored) {}
        player.sendMessage("Monster class not found " + str);
        if (!Monster.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("Unable to spawn monster "+clazz.getName()+": class does not implement Monster");
        }
        Monster monster = (Monster) context.injector().getInstance(clazz);
        monster.spawn(player.getLocation());
        player.sendMessage("Spawned " + clazz.getName());
    }

}