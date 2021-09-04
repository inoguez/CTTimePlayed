package net.crafttorch.cttimeplayed;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Task extends BukkitRunnable {
    private final CTTimePlayed instance;
    private final Player player;

    public Task(CTTimePlayed instance, Player player) {
        this.instance = instance;
        this.player = player;
    }

    @Override
    public void run() {
        UUID uuid = player.getUniqueId();
        if (!getAfkStatus().get(uuid)){
            CTTimePlayed.db.setPlayerTime(uuid, "00:00:01", false);
        }
    }

    public HashMap<UUID, Boolean> getAfkStatus(){
        return instance.getAfkStatus();
    }

}
