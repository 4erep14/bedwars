package com.test.bedwars.events;

import com.google.protobuf.MapEntry;
import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.scoreboards.ScoreboardUtils;
import com.test.bedwars.stats.BedwarsPlayer;
import com.test.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerLoginEventListener implements Listener {

    private GameManager gameManager;

    public PlayerLoginEventListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPrelogin(AsyncPlayerPreLoginEvent event) {
        if(gameManager.getState().equals(GameState.RESET)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You can't join while a world is resetting");
            return;
        }
        if(Bukkit.getOfflinePlayer(event.getUniqueId()).isOp()) {
            return;
        }
        if(gameManager.getState().equals(GameState.ACTIVE) || gameManager.getState().equals(GameState.STARTING)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You can't join the server when the game has already started");
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        gameManager.getMatch().addPlayer(new BedwarsPlayer(player));
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(gameManager.getGameWorld().getLobbyPosition());
        List<Island> islands = gameManager.getGameWorld().getIslands();
        int i;
        for(i = 0; i < islands.size(); ++i) {
            if (!islands.get(i).isTeamFull()) {
                islands.get(i).addPlayer(player);
                gameManager.getMatch().setWinner(islands.get(0));
                break;
            }
        }
        ScoreboardUtils.createScoreboard(player, "&aBedWars",
                gameManager.getGameWorld().getIslands().stream().collect(Collectors.toMap(island -> island.getColor().getChatColor() + island.getColor().formattedName(), island -> gameManager.getGameWorld().isIslandActive(island))));
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p == player) continue;
            ScoreboardUtils.updateScoreboard(p, "&aBedWars",
                    gameManager.getGameWorld().getIslands().stream().collect(Collectors.toMap(island -> island.getColor().getChatColor() + island.getColor().formattedName(), island -> gameManager.getGameWorld().isIslandActive(island))));
        }
        if(i == islands.size() - 1) {
            for(Island island : islands) {
                gameManager.getMatch().addTeam(island);
            }
            gameManager.setState(GameState.STARTING); }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
