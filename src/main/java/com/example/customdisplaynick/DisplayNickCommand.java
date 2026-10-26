package com.example.customdisplaynick;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayNickCommand implements CommandExecutor, TabCompleter {

    private final CustomDisplayNickPlugin plugin;

    public DisplayNickCommand(CustomDisplayNickPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /displaynick <set|reset|reload> ...</red>"));
            return true;
        }

        String subAction = args[0].toLowerCase();

        if (subAction.equals("reload")) {
            if (!sender.hasPermission("customdisplaynick.reload")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>У вас нет прав на выполнение этой команды.</red>"));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Конфигурация плагина перезагружена!</green>"));
            return true;
        }

        if (subAction.equals("set")) {
            if (!sender.hasPermission("customdisplaynick.set")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>У вас нет прав на выполнение этой команды.</red>"));
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /displaynick set <игрок> <новый_ник></red>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Игрок не найден.</red>"));
                return true;
            }

            StringBuilder nickBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                nickBuilder.append(args[i]).append(" ");
            }
            String newNick = nickBuilder.toString().trim();

            plugin.getNickManager().setNick(target, newNick);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Кастомный ник установлен!</green>"));
            return true;
        }

        if (subAction.equals("reset")) {
            if (!sender.hasPermission("customdisplaynick.reset")) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>У вас нет прав на выполнение этой команды.</red>"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Использование: /displaynick reset <игрок></red>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Игрок не найден.</red>"));
                return true;
            }

            plugin.getNickManager().resetNick(target);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Ник сброшен до стандартного.</green>"));
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("customdisplaynick.set")) completions.add("set");
            if (sender.hasPermission("customdisplaynick.reset")) completions.add("reset");
            if (sender.hasPermission("customdisplaynick.reload")) completions.add("reload");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("reset"))) {
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }

        return completions;
    }
}
