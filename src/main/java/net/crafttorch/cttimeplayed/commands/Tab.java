package net.crafttorch.cttimeplayed.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Tab implements TabCompleter {
    public List<String> onTabComplete(final @NotNull CommandSender commandSender, final Command command, final @NotNull String s, final String[] strings) {
        if (command.getName().equalsIgnoreCase("setplayertime")) {
            if (strings.length == 0) {
                return null;
            }
            if (strings.length == 1) {
                final List<String> lista = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    lista.add(p.getName());
                }
                return lista;
            }
            if (strings.length == 2) {
                final List<String> lista = new ArrayList<>();
                lista.add("hh{32}:mm:ss");
                return lista;
            }
        }
        if (command.getName().equalsIgnoreCase("getplayertime")) {
            if (strings.length == 0) {
                return null;
            }
            if (strings.length == 1) {
                final List<String> lista = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    lista.add(p.getName());
                }
                return lista;
            }
        }

        return null;
    }
}
