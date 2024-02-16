package com.test.bedwars.events;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.gui.SetupWizardIslandSelectorGUI;
import com.test.bedwars.worlds.Island;
import com.test.bedwars.worlds.generators.Generator;
import com.test.bedwars.worlds.generators.GeneratorType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerItemInteractListener implements Listener {

    private GameManager gameManager;

    public PlayerItemInteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onInteractWithSetupWizardOnItem(PlayerInteractEvent event) {
        if(!event.hasItem() || !gameManager.getSetupWizardManager().isInWizard(event.getPlayer()) || event.getItem() == null || !event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();

        String itemName = event.getItem().getItemMeta().getDisplayName();
        itemName = ChatColor.stripColor(itemName);

        Location current = player.getLocation();

        Location clicked = (event.getClickedBlock() != null) ? event.getClickedBlock().getLocation() : null;

        Island island = gameManager.getSetupWizardManager().getIsland(player);

        switch (itemName.toLowerCase()) {
            case "set brick generator":
                Generator brickGenerator = new Generator(current, GeneratorType.BRICK);
                gameManager.getConfigurationManager().saveGenerator(player.getWorld().getName(), brickGenerator);
                player.sendMessage("Set brick generator");
                break;
            case "set iron generator":
                Generator ironGenerator = new Generator(current, GeneratorType.IRON);
                gameManager.getConfigurationManager().saveGenerator(player.getWorld().getName(), ironGenerator);
                player.sendMessage("Set iron generator");
                break;
            case "set gold generator":
                Generator goldGenerator = new Generator(current, GeneratorType.GOLD);
                gameManager.getConfigurationManager().saveGenerator(player.getWorld().getName(), goldGenerator);
                player.sendMessage("Set gold generator");
                break;
            case "change island":
                SetupWizardIslandSelectorGUI gui = new SetupWizardIslandSelectorGUI(gameManager);
                gameManager.getGuiManager().setGUI(player, gui);
                break;
            case "set shop location":
                island.setShopEntityLocation(current);
                break;
            case "set spawn location":
                island.setSpawnLocation(current);
                break;
            case "set bed location":
                if(clicked != null) island.setBedLocation(clicked);
                break;
            case "save island":
                gameManager.getConfigurationManager().saveIsland(island);
                gameManager.getSetupWizardManager().worldSetupWizard(player, island.getGameWorld());
                break;
            default: return;
        }

        event.setCancelled(true);
    }
}
