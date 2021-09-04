package net.crafttorch.cttimeplayed.db;

import net.crafttorch.cttimeplayed.CTTimePlayed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private CTTimePlayed plugin;
    private Connection connection;

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL(CTTimePlayed plugin) {
        this.plugin = plugin;
        this.host = plugin.getCustomConfig().getString("DB.host");
        this.port = plugin.getCustomConfig().getString("DB.port");
        this.database = plugin.getCustomConfig().getString("DB.database");
        this.username = plugin.getCustomConfig().getString("DB.username");
        this.password = plugin.getCustomConfig().getString("DB.password");
    }

    public boolean isConnected(){
        return (connection != null);
    }

    public void connect () throws ClassNotFoundException, SQLException {
        if (!isConnected()){
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=false",
                    username, password);
        }
    }

    public void disconnect(){
        if (isConnected()){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection(){
        return connection;
    }
}
