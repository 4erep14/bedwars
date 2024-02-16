package com.test.bedwars.worlds;

import com.test.bedwars.worlds.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Wool;

import java.util.Map;

public enum TeamColor {
    RED,
    BLUE;

    public String formattedName() {
        String caps = this.toString();
        return String.valueOf(caps.charAt(0)).toUpperCase() + caps.substring(1).toLowerCase();
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(this.toString());
    }

    public ItemBuilder woolMaterial() {
        ItemBuilder teamWoolMaterial = new ItemBuilder(Material.WOOL);
        switch (this) {
            case RED:
                teamWoolMaterial.setDurability((short) 14);
                break;
            case BLUE:
                teamWoolMaterial.setDurability((short) 11);
                break;
        }
        return teamWoolMaterial;
    }
}
