package com.test.bedwars.worlds;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.scoreboards.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartCountdown extends BukkitRunnable {

    private GameManager gameManager;
    private int countdown;

    public GameStartCountdown(GameManager gameManager, int countdownInSeconds) {
        this.gameManager = gameManager;
        this.countdown = countdownInSeconds;
    }

    @Override
    public void run() {
        if (countdown > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketUtils.sendTitle(player, "Game starts in " + countdown + "...", 5, 10, 5);
            }
            countdown--;
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketUtils.sendTitle(player, "Game has been started", 10, 20, 10);
            }
            gameManager.setState(GameState.ACTIVE);
            cancel();
        }
        for(Island island : gameManager.getGameWorld().getIslands()) {
          //  if(!island.isTeamFull()) cancel();
        }
    }

    public void start() {
        // Schedule the task to run every second (20 ticks)
        runTaskTimer(gameManager.getPlugin(), 0, 20);
    }
}
