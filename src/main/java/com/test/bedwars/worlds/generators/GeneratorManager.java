package com.test.bedwars.worlds.generators;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GeneratorManager {
    private GameManager gameManager;
    private Map<Generator, Long> lastDropTimes;

    public GeneratorManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.lastDropTimes = new HashMap<>();
        initializeGenerators();
        startResourceDropCheck();
    }

    private void initializeGenerators() {
        for(Generator generator : gameManager.getGameWorld().getGenerators()) {
            lastDropTimes.put(generator, System.currentTimeMillis());
        }
    }

    private void startResourceDropCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.getState().equals(GameState.ACTIVE)) return;


                long currentTime = System.currentTimeMillis();
                for (Map.Entry<Generator, Long> entry : lastDropTimes.entrySet()) {
                    Generator generator = entry.getKey();
                    long lastDropTime = entry.getValue();
                    long interval = generator.getInterval();

                    if (currentTime - lastDropTime >= interval * 1000) {
                        gameManager.getGameWorld().getWorld().dropItemNaturally(generator.getLocation(), new ItemStack(generator.getType().getMaterial(), 1));
                        lastDropTimes.put(generator, currentTime);
                    }
                }
            }
        }.runTaskTimer(gameManager.getPlugin(), 0L, 20L);
    }
}