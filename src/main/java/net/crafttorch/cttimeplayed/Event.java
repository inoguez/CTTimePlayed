package net.crafttorch.cttimeplayed;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Event implements Listener {
    private final CTTimePlayed instance;

    private final HashMap <UUID, Task> playerTask = new HashMap<>();
    private final HashMap <UUID, BukkitTask> afkTask = new HashMap<>();
    private final HashMap <UUID, Location> lastLocation = new HashMap<>();


    private final String broadcast_prefix;
    private final String message_color;
    private final Boolean custom_font;

    public Event(CTTimePlayed instance) {
        this.instance = instance;
        this.broadcast_prefix = Objects.requireNonNull(instance.getCustomConfig().getString("broadcast_prefix")).replaceAll("&", "§");
        this.message_color = Objects.requireNonNull(instance.getCustomConfig().getString("message_color")).replaceAll("&", "§");
        this.custom_font = instance.getCustomConfig().getBoolean("custom_font");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Task task = new Task(instance, player);


        CTTimePlayed.db.insertPlayer(player);

        instance.getAfkStatus().put(uuid, false);

        task.runTaskTimer(instance,20,20);
        playerTask.put(uuid,task);

        afkCheck(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        //playerTask task leave process
        playerTask.get(uuid).cancel();
        playerTask.remove(uuid);
        //afk task leave process
        afkTask.get(uuid).cancel();
        afkTask.remove(uuid);

        instance.getAfkStatus().remove(uuid);

        lastLocation.remove(uuid);
    }

     @EventHandler
     public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (instance.getAfkStatus().containsKey(uuid)){
            if (instance.getAfkStatus().get(uuid)){
                instance.getAfkStatus().put(uuid, false);
                TextComponent noLongerAfktext = new TextComponent(broadcast_prefix + " " + player.getName() + message_color + " is no longer afk.");
                if (custom_font) {
                    noLongerAfktext.setFont("minecraft:uniform");
                }
                Bukkit.getServer().spigot().broadcast(noLongerAfktext);
            }
        }
     }

    public void afkCheck(Player player){
        UUID uuid = player.getUniqueId();
        TextComponent afkText = new TextComponent(broadcast_prefix + " " + player.getName() + message_color + " is now afk.");
        if (custom_font) {
            afkText.setFont("minecraft:uniform");
        }
        if (instance.getAfkStatus().containsKey(uuid)) {
            BukkitTask afkT = new BukkitRunnable() {
                @Override
                public void run() {
                    if (instance.getAfkStatus().get(uuid)) return;
                    Location newLoc = player.getLocation();
                    if (lastLocation.containsKey(uuid)) {
                        Location lastLoc = lastLocation.get(uuid);
                        int x1 = Math.toIntExact(lastLoc.getBlockX());
                        int y1 = Math.toIntExact(lastLoc.getBlockY());
                        int z1 = Math.toIntExact(lastLoc.getBlockZ());

                        int x2 = Math.toIntExact(newLoc.getBlockX());
                        int y2 = Math.toIntExact(newLoc.getBlockY());
                        int z2 = Math.toIntExact(newLoc.getBlockZ());

                        if (x2 == x1 && y2 == y1 && z2 == z1) {
                            instance.getAfkStatus().put(uuid, true);
                            Bukkit.getServer().spigot().broadcast(afkText);
                        }
                    }
                    lastLocation.put(uuid, newLoc);
                    //Bukkit.broadcastMessage("El jugador: " + player.getName() + ", ha jugado: " + instance.db.getPlayerTime(uuid) + " y su status de afk es: " + instance.getAfkStatus().get(uuid) );
                }
            }.runTaskTimer(instance, 0, 20 * 60 * 5);
            afkTask.put(uuid, afkT);
        }
    }
}
