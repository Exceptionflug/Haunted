package de.exceptionflug.haunted.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 23.09.2021
 *
 * @author Exceptionflug
 */
public final class HighlightUtil {

    private static final Map<ChatColor, Team> TEAM_MAP = new ConcurrentHashMap<>();

    private HighlightUtil() {}

    public static List<Entity> highlight(CuboidRegion region, ChatColor color) {
        List<Entity> out = new ArrayList<>();
        for (Location location : region.locations()) {
            Slime slime = location.getWorld().spawn(location.add(0.5, 0, 0.5), Slime.class, slime1 -> {
                slime1.setSize(2);
                slime1.setAI(false);
                slime1.setInvisible(true);
                slime1.setGravity(false);
                slime1.setInvulnerable(true);
                slime1.setGlowing(true);
                slime1.setSilent(true);
            });
            out.add(slime);
            Team team = TEAM_MAP.computeIfAbsent(color, color1 -> {
                Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(color.name());
                if (t == null) {
                    t = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(color.name());
                }
                t.setColor(color);
                t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                return t;
            });
            team.addEntry(slime.getUniqueId().toString());
        }
        return out;
    }

}
