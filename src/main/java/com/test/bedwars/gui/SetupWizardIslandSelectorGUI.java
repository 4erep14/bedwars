package com.test.bedwars.gui;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.TeamColor;
import com.test.bedwars.worlds.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SetupWizardIslandSelectorGUI implements GUI {

    private Inventory inventory;
    private GameManager gameManager;

    public SetupWizardIslandSelectorGUI(GameManager gameManager) {
        inventory = Bukkit.createInventory(null, 27,  getName());
        this.gameManager = gameManager;

        for(TeamColor color : TeamColor.values()) {
            inventory.addItem(
                    color.woolMaterial().setName(color.getChatColor() + color.formattedName()).toItemStack()
            );
        }

        inventory.addItem(new ItemBuilder(Material.BARRIER).setName("&aExit").toItemStack());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return "Select Island";
    }

    @Override
    public GUI handleClick(Player player, ItemStack itemStack, InventoryView view) {
        if(!gameManager.getSetupWizardManager().isInWizard(player)) return null;
        TeamColor clickedColor = null;

        String itemName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
        for(TeamColor color : TeamColor.values()) {
            if (itemName.equalsIgnoreCase(color.formattedName())) {
                clickedColor = color;
                break;
            }
        }

        if(clickedColor != null)
            gameManager.getSetupWizardManager().teamSetupWizard(player, clickedColor);
        else
            gameManager.getSetupWizardManager().worldSetupWizard(player, gameManager.getSetupWizardManager().getWorld(player));

        return null;
    }

    @Override
    public GUI handleShiftClick(Player player, ItemStack itemStack, InventoryView view) {
        return handleClick(player, itemStack, view);
    }

    @Override
    public boolean isInventory(InventoryView view) { return view.getTitle().equals(getName()); }
}
