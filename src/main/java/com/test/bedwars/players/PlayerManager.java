package com.test.bedwars.players;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.Island;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private GameManager gameManager;

    private Map<Player, Location> playersToRespawnMap = new HashMap<>();

    public PlayerManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void teleportToIslandSpawn(Player player) {
        for(Island island : gameManager.getGameWorld().getIslands()) {
            if(island.isMember(player)) {
                player.setHealth(20);
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
                player.teleport(island.getSpawnLocation());
            }
        }
    }

    public Map<Player, Location> getPlayersToRespawnMap() { return playersToRespawnMap; }

    public void addToRespawn(Player player, Location location) {
        playersToRespawnMap.put(player, location);
    }

    public void markRespawned(Player player) {
        playersToRespawnMap.remove(player);
    }
}
