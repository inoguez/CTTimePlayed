package net.crafttorch.cttimeplayed.commands;

import net.crafttorch.cttimeplayed.CTTimePlayed;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class Command implements CommandExecutor {
    private CTTimePlayed plugin;
    private final String broadcast_prefix;
    private final String message_color;
    private final Boolean custom_font;

    public Command(CTTimePlayed plugin) {
        this.plugin = plugin;
        this.broadcast_prefix = Objects.requireNonNull(plugin.getCustomConfig().getString("broadcast_prefix")).replaceAll("&", "§");
        this.message_color = Objects.requireNonNull(plugin.getCustomConfig().getString("message_color")).replaceAll("&", "§");
        this.custom_font = plugin.getCustomConfig().getBoolean("custom_font");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("setplayertime")) {
                if (args.length == 0) return false;
                if (args.length == 1) return false;
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        Pattern p = Pattern.compile("\\d{1,32}:\\d{2}:\\d{2}");
                        if (p.matcher(args[1]).matches()) {
                            CTTimePlayed.setPlayerTime(target, args[1]);
                            TextComponent text = new TextComponent(broadcast_prefix + message_color + " Adjusted time of " + "§f" + player.getName() + message_color + " at " + CTTimePlayed.getPlayerTime(player));
                            if (custom_font) {
                                text.setFont("minecraft:uniform");
                            }
                            player.spigot().sendMessage(text);
                        } else {
                            player.sendMessage("Correct pattern: hh{32}:mm:ss");
                        }
                    } else {
                        player.sendMessage("Select a valid player name");
                    }
                    return true;
                }
            }
            if (command.getName().equalsIgnoreCase("getplayertime")) {
                if (args.length == 0) {
                    TextComponent text = new TextComponent(broadcast_prefix + message_color + " Time played " + CTTimePlayed.getPlayerTime(player));
                    if (custom_font) {
                        text.setFont("minecraft:uniform");
                    }
                    player.spigot().sendMessage(text);
                    return true;
                }
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        TextComponent text = new TextComponent(broadcast_prefix + message_color + " Time played of " + "§f" + target.getName() + message_color + " is " + CTTimePlayed.getPlayerTime(target));
                        if (custom_font) {
                            text.setFont("minecraft:uniform");
                        }
                        player.spigot().sendMessage(text);
                    } else {
                        player.sendMessage("Select a valid player name");
                    }
                    return true;
                }
            }
            if (command.getName().equalsIgnoreCase("gettoptimelist")) {
                if (args.length == 0) {
                    int linesPerPage = 5;
                    int pageNumber = 1;
                    List<String> topList = CTTimePlayed.getTopTimeList();
                    int pageAmount = topList.size() / linesPerPage;
                    int totalpages = (int) Math.ceil(pageAmount);
                    int count = 1;

                    for (int i = (0); i < (pageNumber * linesPerPage) && i < topList.size(); i++) {
                        String indexString = topList.get(i);
                        String[] parts = indexString.split(":");
                        TextComponent text = new TextComponent(message_color + count + " §f " + parts[0] + message_color + ", " + parts[1]);
                        if (custom_font) {
                            text.setFont("minecraft:uniform");
                        }
                        player.spigot().sendMessage(text);
                        count++;
                    }
                    return true;
                }
            }
        return false;
    }
}
