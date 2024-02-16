package com.test.bedwars.events;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gui.GUI;
import com.test.bedwars.gui.ShopGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickListener implements Listener {

    private GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onCLick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;;

        Player player = (Player) event.getWhoClicked();

        GUI gui = gameManager.getGuiManager().getOpenGUI(player);
        if(gui == null) return;

        event.setCancelled(true);

        GUI newGUI = event.isShiftClick() ? gui.handleShiftClick(player, event.getCurrentItem(), event.getView()) :
                gui.handleClick(player, event.getCurrentItem(), event.getView());
        if(!(newGUI instanceof ShopGUI)) event.getView().close();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        gameManager.getGuiManager().clear(player);
    }
}
