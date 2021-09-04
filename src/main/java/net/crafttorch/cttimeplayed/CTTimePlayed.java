package net.crafttorch.cttimeplayed;

import net.crafttorch.cttimeplayed.commands.Command;
import net.crafttorch.cttimeplayed.commands.Tab;
import net.crafttorch.cttimeplayed.db.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CTTimePlayed extends JavaPlugin {
    private FileConfiguration customConfig;
    public static Database db;
    private final HashMap<UUID, Boolean> afkStatus = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        createCustomConfig();
        cVersion();
        db = new Database(this);
        SQLconnect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        db.disconnect();
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        File customConfigFile = new File(getDataFolder(), "timePlayed.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("timePlayed.yml", false);
        }

        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void SQLconnect(){
        try {
            db.connect();
        } catch (ClassNotFoundException | SQLException e) {
            getServer().getConsoleSender().sendMessage("§f[§6TimePlayed§f]" + " §4Can't connect to database");
        }

        if (db.isConnected()){
            getServer().getConsoleSender().sendMessage("§f[§6TimePlayed§f]" + " §aConnected to database");
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new PapiExpansion(this).register();
            }
            db.createTable();
            getServer().getPluginManager().registerEvents(new Event(this), this);
            Objects.requireNonNull(getCommand("setplayertime")).setExecutor(new Command(this));
            Objects.requireNonNull(getCommand("getplayertime")).setExecutor(new Command(this));
            Objects.requireNonNull(getCommand("gettoptimelist")).setExecutor(new Command(this));
            Objects.requireNonNull(getCommand("setplayertime")).setTabCompleter(new Tab());
            Objects.requireNonNull(getCommand("getplayertime")).setTabCompleter(new Tab());
            Objects.requireNonNull(getCommand("gettoptimelist")).setTabCompleter(new Tab());
        }
    }
    public HashMap<UUID, Boolean> getAfkStatus() {
        return afkStatus;
    }

    private void cVersion(){
        String version = Bukkit.getVersion();
        if (version.contains("1.16") || version.contains("1.17")){
            getServer().getConsoleSender().sendMessage("§f[§6TimePlayed§f]" + " §5Running on 1.16 + server");
            getCustomConfig().set("custom_font",true);
        }
    }


    public static void setPlayerTime(Player player, String time){
        db.setPlayerTime(player.getUniqueId(), time, true);
    }

    public static String getPlayerTime(Player player){
        String time = db.getPlayerTime(player.getUniqueId());
        String[] parts = time.split(":");
        return parts[0] + "hr, " + parts[1] + "min, " + parts[2] + "sec";
    }

    public static String getPlayerTime(Player player, Boolean raw){
        String time = db.getPlayerTime(player.getUniqueId());
        String[] parts = time.split(":");
        return parts[0] + ":" + parts[1] + ":" + parts[2];
    }

    public static List<String> getTopTimeList(){
        return db.getTopTime();
    }

    public static String formatTime(int seconds) {
        return String.format("%d:%d:%d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public static int formatTimeToSec(String time){
        String[] parts1 = time.split(":");
        return (Integer.parseInt(parts1[0]) * 3600) + (Integer.parseInt(parts1[1]) * 60) + Integer.parseInt(parts1[2]);
    }

    public static String sumFormatTime(String time1, String time2){
        int t1 = formatTimeToSec(time1);
        int t2 = formatTimeToSec(time2);
        int ft = t1 + t2;

        return formatTime(ft);
    }

}
