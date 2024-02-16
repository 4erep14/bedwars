package com.test.bedwars.events;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gui.SetupWizardIslandSelectorGUI;
import com.test.bedwars.gui.ShopGUI;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ShopInteractListener implements Listener {

    private GameManager gameManager;

    public ShopInteractListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) return;
        Villager interacted = (Villager) event.getRightClicked();
        if (interacted.getCustomName().equals("Shop")) {
            ShopGUI gui = new ShopGUI(gameManager, event.getPlayer());
            gameManager.getGuiManager().setGUI(event.getPlayer(), gui);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;

        Villager villager = (Villager) event.getEntity();
        if (villager.getCustomName() != null && villager.getCustomName().equalsIgnoreCase("shop")) {
            event.setCancelled(true);
        }
    }
}
