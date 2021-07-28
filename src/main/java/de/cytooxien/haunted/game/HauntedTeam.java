package de.cytooxien.haunted.game;

import de.cytooxien.strider.game.team.GameTeam;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

/**
 * Date: 12.07.2021
 *
 * @author Exceptionflug
 */
public class HauntedTeam extends GameTeam<HauntedGame, HauntedPlayer, HauntedTeam> {

    private final Team scoreboardTeam;

    public HauntedTeam(HauntedGame game, int index) {
        super(game, index);
        this.scoreboardTeam = game.getTeamManager().getScoreboard().registerNewTeam(Integer.toString(index));
        this.scoreboardTeam.setPrefix("§a");
        this.scoreboardTeam.setColor(ChatColor.GREEN);
        this.scoreboardTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    public boolean alive() {
        for (HauntedPlayer player : getMembers()) {
            if (!player.dead()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        return "§aTeam Spieler";
    }

    @Override
    public String getPrefix() {
        return "§a";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

}
