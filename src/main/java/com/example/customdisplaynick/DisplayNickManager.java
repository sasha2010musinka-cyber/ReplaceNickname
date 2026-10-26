package com.example.customdisplaynick;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class DisplayNickManager {

    private final CustomDisplayNickPlugin plugin;
    private static final String TEAM_PREFIX = "cdn_";

    public DisplayNickManager(CustomDisplayNickPlugin plugin) {
        this.plugin = plugin;
    }

    public void setNick(Player target, String rawNickInput) {
        UUID uuid = target.getUniqueId();
        plugin.getConfig().set("nicks." + uuid, rawNickInput);
        plugin.saveConfig();

        applyNick(target, rawNickInput);
    }

    public void applyNick(Player target, String rawNickInput) {
        Scoreboard scoreboard = target.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            target.setScoreboard(scoreboard);
        }

        String teamName = TEAM_PREFIX + target.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        String processedInput = rawNickInput;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            processedInput = PlaceholderAPI.setPlaceholders(target, rawNickInput);
        }

        Component parsedNick = MiniMessage.miniMessage().deserialize(processedInput);
        team.suffix(parsedNick);

        if (!team.hasEntry(target.getName())) {
            team.addEntry(target.getName());
        }
    }

    public void resetNick(Player target) {
        UUID uuid = target.getUniqueId();
        plugin.getConfig().set("nicks." + uuid, null);
        plugin.saveConfig();

        removeNickTeam(target);
    }

    public void removeNickTeam(Player target) {
        Scoreboard scoreboard = target.getScoreboard();
        String teamName = TEAM_PREFIX + target.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.removeEntry(target.getName());
            team.unregister();
        }
    }

    public void loadPlayerNick(Player player) {
        UUID uuid = player.getUniqueId();
        if (plugin.getConfig().contains("nicks." + uuid)) {
            String nick = plugin.getConfig().getString("nicks." + uuid);
            if (nick != null && !nick.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> applyNick(player, nick), 10L);
            }
        }
    }

    public void cleanupAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeNickTeam(player);
        }
    }
}
