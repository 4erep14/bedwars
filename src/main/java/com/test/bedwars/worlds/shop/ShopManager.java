package com.test.bedwars.worlds.shop;

import com.test.bedwars.gamemanager.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopManager {
    private GameManager gameManager;

    private Map<String, ShopItem> itemToNameMap = new HashMap<>();

    public ShopManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public ItemStack createShopItem(ShopItem shopItem, Player player) {
        ItemStack item = new ItemStack(shopItem.getMaterial());
        if (item.getType().equals(Material.WOOL)) {
            item = gameManager.getGameWorld().getIslands().stream().filter( island-> island.getPlayers().contains(player) ).findAny().orElse(null).getColor().woolMaterial().toItemStack();
        }
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("Price: " + + shopItem.getPrice() + " " + shopItem.getCurrency().getMaterial().toString().replace("_", " ").toLowerCase());

        meta.setLore(lore);
        meta.setDisplayName(shopItem.getName());

        item.setItemMeta(meta);
        itemToNameMap.put(shopItem.getName(), shopItem);
        return item;
    }

    public ShopItem getShopItem(String name) { return itemToNameMap.get(name); }

    public void processTransaction(Player player, ItemStack itemStack, int amount) {

        ShopItem shopItem = getShopItem(itemStack.getItemMeta().getDisplayName());

        if(!hasEnoughCurrency(player, shopItem.getPrice() * amount, shopItem.getCurrency().getMaterial())) {
            player.sendMessage("You cannot buy it yet");
            return;
        }
        ItemStack currency = new ItemStack(shopItem.getCurrency().getMaterial(), shopItem.getPrice() * amount);
        player.getInventory().removeItem(currency);
        if(itemStack.getData().getItemType().toString().contains("SWORD")) {
            if(player.getInventory().contains(Material.WOOD_SWORD)) {
                player.getInventory().removeItem(new ItemStack(Material.WOOD_SWORD));
            }
        }

        ItemStack itemToGive = itemStack.clone();
        clearLoreAndCustomName(itemToGive);
        for(int i = 0; i < amount; ++i) {
            player.getInventory().addItem(itemToGive);
        }
    }

    public void processTransaction(Player player, ItemStack itemStack) {
        processTransaction(player, itemStack, 1);
    }

    private boolean hasEnoughCurrency(Player player, int price, Material currencyMaterial) {
        return player.getInventory().containsAtLeast(new ItemStack(currencyMaterial), price);
    }

    public int itemsCanBuy(Player player, int price, Material material) {
        return countPlayerCurrency(player, material) / price;
    }

    private int countPlayerCurrency(Player player, Material currencyMaterial) {
        ItemStack[] items = player.getInventory().getContents();
        int count = 0;
        for (ItemStack item : items) {
            if (item != null && item.getType() == currencyMaterial) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private void clearLoreAndCustomName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(new ArrayList<>());
            meta.setDisplayName(null);
            item.setItemMeta(meta);
        }
    }
}
