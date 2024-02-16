package com.test.bedwars.events;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.stats.BedwarsPlayer;
import com.test.bedwars.worlds.Island;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import javax.persistence.EntityNotFoundException;

public class BlockUpdateListener implements Listener {

    private GameManager gameManager;

    public BlockUpdateListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (shouldSkipEvent(player)) return;
        if (shouldCancelEvent(event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        Material type = event.getBlock().getType();
        BedwarsPlayer bedwarsPlayer = gameManager.getMatch().getPlayers().stream().filter(p -> p.getPlayer() == event.getPlayer()).findAny().orElseThrow(EntityNotFoundException::new);
        if (type.equals(Material.WOOL)) {
            bedwarsPlayer.incrementWoolBlocksDestroyed();
            return;
        }
        event.setCancelled(true);

        if (type.equals(Material.BED_BLOCK) || type.equals(Material.BED)) {
            Location location = event.getBlock().getLocation();
            Island island = gameManager.getGameWorld().getIslandForBedLocation(location);
            if (island != null && !island.isMember(player)) {
                event.getBlock().setType(Material.AIR);
                bedwarsPlayer.incrementBedsDestroyed();
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(shouldSkipEvent(event.getPlayer())) return;

        if(shouldCancelEvent(event.getBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean shouldCancelEvent(Block block) {

        return !gameManager.getState().equals(GameState.ACTIVE) || block.getLocation().getBlockY() > 100;
    }

    private boolean shouldSkipEvent(Player player) {
        return player.getGameMode().equals(GameMode.CREATIVE) ||
                (!gameManager.getState().equals(GameState.ACTIVE) && !gameManager.getState().equals(GameState.ENDGAME));
    }

}
