package com.test.bedwars.setup;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.TeamColor;
import com.test.bedwars.worlds.GameWorld;
import com.test.bedwars.worlds.Island;
import com.test.bedwars.worlds.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetupWizardManager {

    public Map<Player, Island> playerToIslandMap = new HashMap<>();
    public Map<Player, GameWorld> playerToGameWorldMap = new HashMap<>();

    private GameManager gameManager;

    public SetupWizardManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean isInWizard(Player player) {
        return playerToGameWorldMap.containsKey(player);
    }

    public void worldSetupWizard(Player player, GameWorld world) {
        player.getInventory().clear();

        player.getInventory().addItem(
                new ItemBuilder(Material.CLAY_BRICK).setName("&aSet Brick Generator").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.IRON_INGOT).setName("&aSet Iron Generator").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.GOLD_INGOT).setName("&aSet Gold Generator").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.STICK).setName("&aChange Island").toItemStack()
        );
        playerToGameWorldMap.put(player, world);
    }

    public void activatedSetupWizard(Player player, GameWorld world) {
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);
        player.teleport(new Location(world.getWorld(), 0, 64, 0));

        worldSetupWizard(player, world);
    }

    public void teamSetupWizard(Player player, TeamColor teamColor) {
        player.getInventory().clear();

        player.getInventory().addItem(
                new ItemBuilder(Material.BOWL).setName("&aSet Spawn Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BED).setName("&aSet Bed Location").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.EGG).setName("&aSet Shop Location").toItemStack()
        );
        player.getInventory().addItem(
                teamColor.woolMaterial().setName("&aChange Island").toItemStack()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.RED_MUSHROOM).setName("&aSave Island").toItemStack()
        );

        Island island = new Island(getWorld(player), teamColor);
        playerToIslandMap.put(player, island);
    }

    public GameWorld getWorld(Player player) {
        return playerToGameWorldMap.get(player);
    }
    public Island getIsland(Player player) {
        return playerToIslandMap.get(player);
    }

    public void removeFromWizard(Player player) {
        player.teleport(new Location(Bukkit.getWorld("world"), 0, 70 ,0));

    }
}
