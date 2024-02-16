package com.test.bedwars.worlds;

import com.test.bedwars.worlds.generators.Generator;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Island {

    private GameWorld gameWorld;
    private TeamColor color;
    private static final int TEAM_CAPACITY = 1;

    private List<Player> players = new ArrayList<>();

    private Location shopEntityLocation = null;
    private Location spawnLocation = null;
    private Location bedLocation = null;

    private List<Generator> generators = new ArrayList<>();

    public Island(GameWorld gameWorld, TeamColor color) {
        this.gameWorld = gameWorld;
        this.color = color;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Location getShopEntityLocation() {
        return shopEntityLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getBedLocation() {
        return bedLocation;
    }

    public List<Generator> getGenerators() {
        return generators;
    }

    public TeamColor getColor() { return color; }


    public void setBedLocation(Location bedLocation) { this.bedLocation = bedLocation; }

    public void setShopEntityLocation(Location shopEntityLocation) { this.shopEntityLocation = shopEntityLocation; }

    public void setSpawnLocation(Location spawnLocation) { this.spawnLocation = spawnLocation; }

    public void addGenerator(Generator generator) { generators.add(generator); }

    public void setGenerators(List<Generator> generators) {
        this.generators = generators;
    }

    public int alivePlayerCount() {
        return players.stream().filter(player -> player.getGameMode() != GameMode.SPECTATOR).toArray().length;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isMember(Player player) { return players.contains(player); }

    public boolean isBedPlaced() {
        return getBedLocation().getBlock().getType().toString().contains("BED");
    }

    public void addPlayer(Player player) { players.add(player); }

    public boolean isTeamFull() { return players.size() == TEAM_CAPACITY; }
}
