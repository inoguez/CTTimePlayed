package net.crafttorch.cttimeplayed;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {
    CTTimePlayed plugin;

    public PapiExpansion(CTTimePlayed plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cttimeplayed";
    }

    @Override
    public @NotNull String getAuthor() {
        return "2GramsIn";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if(params.equalsIgnoreCase("getplayertime")){
            return CTTimePlayed.getPlayerTime(player);
        }

        return null;
    }
}
