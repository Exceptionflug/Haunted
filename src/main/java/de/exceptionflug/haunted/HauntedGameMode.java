package de.exceptionflug.haunted;

import de.exceptionflug.haunted.game.HauntedMap;
import de.exceptionflug.haunted.game.HauntedPlayer;
import de.exceptionflug.haunted.phases.HauntedIngamePhase;
import de.exceptionflug.haunted.phases.HauntedLobbyPhase;
import de.exceptionflug.projectvenom.game.Configurator;
import de.exceptionflug.projectvenom.game.GameContext;
import de.exceptionflug.projectvenom.game.GameContextBuilder;
import de.exceptionflug.projectvenom.game.behaviours.RoundBasedGameBehaviour;
import de.exceptionflug.regisseur.Cutscene;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedGameMode extends JavaPlugin {

    private static GameContext context;

    @Override
    public void onEnable() {
        context = GameContextBuilder.create(this)
                .configurator(Configurator.create().options(HauntedOptions.class))
                .behaviour(RoundBasedGameBehaviour.create(HauntedIngamePhase.class, HauntedLobbyPhase.class))
                .gamePlayerFactory(HauntedPlayer::new)
                .gameMapFactory(HauntedMap::new)
                .createGameContext();
        DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        minecraftServer.getAdvancements().advancements = new AdvancementList();
    }

    @Override
    public void onDisable() {
        Cutscene.shutdown();
    }

    public static GameContext getGameContext() {
        return context;
    }
}
