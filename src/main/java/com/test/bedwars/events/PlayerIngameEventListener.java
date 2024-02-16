package com.test.bedwars.events;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.scoreboards.ScoreboardUtils;
import com.test.bedwars.stats.BedwarsPlayer;
import com.test.bedwars.worlds.Island;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerIngameEventListener implements Listener {

    private GameManager gameManager;

    public PlayerIngameEventListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(!gameManager.getState().equals(GameState.ACTIVE)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), () -> {
                player.spigot().respawn();
                player.teleport(gameManager.getGameWorld().getLobbyPosition());
            }, 2);
        }
        BedwarsPlayer bedwarsPlayer = gameManager.getMatch().getPlayers().stream().filter(p -> p.getPlayer() == player).findAny().orElseThrow(EntityNotFoundException::new);
        bedwarsPlayer.incrementDeathsInGame();

        ScoreboardUtils.updateScoreboard(player, "&aBedWars",
                gameManager.getGameWorld().getIslands().stream().collect(Collectors.toMap(island -> island.getColor().getChatColor() + island.getColor().formattedName(), island -> gameManager.getGameWorld().isIslandActive(island))));

        event.setKeepInventory(true);
        List<Island> activeIslands = gameManager.getGameWorld().getActiveIslands();
        if(activeIslands.size() <= 1) {
            gameManager.setState(GameState.ENDGAME);
        }
        for(Island island : activeIslands) {
            if(island.isMember(player.getPlayer())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), () -> {
                    player.getInventory().clear();
                    player.spigot().respawn();
                    gameManager.getPlayerManager().teleportToIslandSpawn(player);
                }, 2);
                return;
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(gameManager.getPlugin(), () -> {
            player.spigot().respawn();
            player.teleport(gameManager.getGameWorld().getLobbyPosition());
            player.setGameMode(GameMode.SPECTATOR);
        }, 2);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().isDead()) return;
        if (event.getPlayer().getLocation().getY() < 0) {
            event.getPlayer().setHealth(0);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!gameManager.getState().equals(GameState.ACTIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityByEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            double damage = event.getFinalDamage();

            BedwarsPlayer bwDamagedPlayer = gameManager.getMatch().getPlayers().stream().filter(p -> p.getPlayer() == damagedPlayer).findAny().orElseThrow(EntityNotFoundException::new);
            bwDamagedPlayer.addToDamageReceived(damage);

            if (event.getDamager() instanceof Player) {
                Player damagingPlayer = (Player) event.getDamager();

                BedwarsPlayer bwDamagingPlayer = gameManager.getMatch().getPlayers().stream().filter(p -> p.getPlayer() == damagingPlayer).findAny().orElseThrow(EntityNotFoundException::new);
                bwDamagingPlayer.addToDamageDealt(damage);
            }
        }
    }
}
