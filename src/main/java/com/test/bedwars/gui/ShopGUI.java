package com.test.bedwars.gui;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.worlds.TeamColor;
import com.test.bedwars.worlds.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI implements GUI {

    private Inventory inventory;
    private GameManager gameManager;

    public ShopGUI(GameManager gameManager, Player player) {
        this.gameManager = gameManager;

        this.inventory = Bukkit.createInventory(null, 27, "Island shop");

        for (ShopItem item : gameManager.getGameWorld().getShopItems()) {
            inventory.addItem(gameManager.getShopManager().createShopItem(item, player));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return "Shop";
    }

    @Override
    public GUI handleClick(Player player, ItemStack itemStack, InventoryView view) {
        if(!gameManager.getState().equals(GameState.ACTIVE)) return null;

        gameManager.getShopManager().processTransaction(player, itemStack);

        return this;
    }

    @Override
    public GUI handleShiftClick(Player player, ItemStack itemStack, InventoryView view) {
        if(!gameManager.getState().equals(GameState.ACTIVE)) return null;

        ShopItem shopItem = gameManager.getShopManager().getShopItem(itemStack.getItemMeta().getDisplayName());

        gameManager.getShopManager().processTransaction(player, itemStack, gameManager.getShopManager().itemsCanBuy(player, shopItem.getPrice(), shopItem.getCurrency().getMaterial()));
        return this;
    }

    @Override
    public boolean isInventory(InventoryView view) { return view.getTitle().equals(getName()); }
}
