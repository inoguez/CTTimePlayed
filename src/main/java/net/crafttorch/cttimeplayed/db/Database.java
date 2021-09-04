package net.crafttorch.cttimeplayed.db;

import net.crafttorch.cttimeplayed.CTTimePlayed;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database extends MySQL{
    private CTTimePlayed plugin;
    private final String table;

    public Database(CTTimePlayed plugin) {
        super(plugin);
        this.plugin = plugin;
        this.table = plugin.getCustomConfig().getString("DB.table_name");
    }

    public void createTable(){
        PreparedStatement ps;
        try {
            ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(" + // make sure to put your table name in here too.
                            "`PLAYER` VARCHAR(100)," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
                            "`UUID` VARCHAR(100)," +
                            "`TIME` VARCHAR (100)," +
                            "PRIMARY KEY (`PLAYER`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
                            ");");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(Player player){
        try{
            UUID uuid = player.getUniqueId();
            if(!exist(uuid)){
                PreparedStatement ps2 = getConnection().prepareStatement("INSERT IGNORE INTO " + table + " (PLAYER,UUID,TIME) VALUES (?,?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.setString(3,"00:00:00");
                ps2.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();

        }
    }

    public boolean exist(UUID uuid){
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, uuid.toString());

            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void setPlayerTime(UUID uuid, String time, Boolean reset){
        try{
            PreparedStatement ps = getConnection().prepareStatement("UPDATE playtime SET TIME=? WHERE UUID=?");
            if (reset){
                ps.setString(1, time);
            }else{
                ps.setString(1, CTTimePlayed.sumFormatTime(getPlayerTime(uuid), time));
            }
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String getPlayerTime(UUID uuid){
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT TIME FROM playtime WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("TIME");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return "Without played time";
    }

    public List<String> getTopTime(){
        List<String> topTimeList = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT TIME,PLAYER FROM playtime ORDER BY TIME DESC");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("PLAYER") != null){
                    String[] parts = rs.getString("TIME").split(":");
                    topTimeList.add(rs.getString("PLAYER") + ": " + parts[0] + " hrs, " + parts[1] + " mins, " + parts[2] + " secs");
                }
            }
            return topTimeList;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
