package com.test.bedwars.worlds;


import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.generators.Generator;
import com.test.bedwars.worlds.shop.ShopItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameWorld {

    private String name;
    private World world;

    private Location lobbyPosition;

    private List<Island> islands = new ArrayList<>();
    private List<Generator> generators = new ArrayList<>();
    private List<ShopItem> shopItems = new ArrayList<>();

    public GameWorld(String name) {
        this.name = name;
    }

    public void loadWorld (GameManager gameManager, boolean loadIntoPlaying,Runnable runnable) {
        File sourceWorldFolder = null;
        try {
            sourceWorldFolder = new File(
                    gameManager.getPlugin().getDataFolder().getCanonicalPath() + File.separator + ".." + File.separator + ".." + File.separator+ name
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        File destinationWorldFolder = new File(sourceWorldFolder.getPath() + (loadIntoPlaying ? "_playing" : ""));

        try {
            copyFolder(sourceWorldFolder, destinationWorldFolder);
        } catch(IOException e) {
            e.printStackTrace();
        }

        WorldCreator creator = new WorldCreator(name + (loadIntoPlaying ? "_playing" : ""));
        world = creator.createWorld();
        world.getName();

        runnable.run();
    }

    public void copyFolder(File src, File destination) throws IOException{
        if (src.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] files = src.list();

            for (String file : files) {
                if (file.equals("uid.dat")) {
                    continue;
                }

                File srcFile = new File(src, file);
                File destinationFile = new File(destination, file);

                copyFolder(srcFile, destinationFile);
            }
        } else {
            Files.copy(src.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void resetWorld() {
        if(world == null) return;

        String worldName = world.getName();

        Bukkit.unloadWorld(world, false);
        File file = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath().replace(".","") + worldName);

        if(delete(file)) {
            Bukkit.getServer().getLogger().info("Deleted" + worldName);
        } else {
            Bukkit.getServer().getLogger().severe("Failed to delete" + worldName);
        }
    }

    private boolean delete(File toDelete) {
        File[] allContents = toDelete.listFiles();

        if(allContents != null) {
            for(File file : allContents) {
                delete(file);
            }
        }

        return toDelete.delete();
    }

    public String getConfigName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public Location getLobbyPosition() {
        return lobbyPosition;
    }

    public Location getSpawnForTeamColor(TeamColor color) {
        return islands.stream().filter(island -> island.getColor().equals(color)).findFirst().map(Island::getSpawnLocation).orElse(null);
    }

    public Island getIslandForBedLocation(Location location) {
        return islands.stream().filter(island -> {
            if (island.getBedLocation().distance(location) < 0.2) return true;

            Location oneExtraZ = location.add(0, 0, 1);
            if (island.getBedLocation().distance(oneExtraZ) < 0.2) return true;

            Location oneLessZ = location.add(0, 0, -1);
            if (island.getBedLocation().distance(oneLessZ) < 0.2) return true;

            Location oneExtraX = location.add(1, 0, 0);
            if (island.getBedLocation().distance(oneExtraX) < 0.2) return true;

            Location oneLessX = location.add(-1, 0, 0);
            return island.getBedLocation().distance(oneLessX) < 0.2;
        }).findFirst().orElse(null);
        }

    public List<Generator> getGenerators() {
        return generators;
    }

    public List<ShopItem> getShopItems() { return shopItems; }

    public void addIsland(Island island) {
        islands.add(island);
    }

    public void addShopItem(ShopItem shopItem) { shopItems.add(shopItem); }

    public List<Island> getActiveIslands() {
        return islands.stream().filter(island -> island.isBedPlaced() && island.alivePlayerCount() != 0).collect(Collectors.toList());
    }

    public boolean isIslandActive(Island island) {
        return this.getActiveIslands().contains(island);
    }

    public List<Island> getIslands() {
        return islands;
    }

    public void setLobbySpawn(Location spawnPosition) {
        this.lobbyPosition = spawnPosition;
    }

    public void setGenerators(List<Generator> generators) { this.generators = generators; }

    public void setShopItems(List<ShopItem> shopItems) { this.shopItems = shopItems; }
}
