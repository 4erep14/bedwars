package com.test.bedwars.worlds.generators;

import org.bukkit.Material;

public enum GeneratorType {
    BRICK,
    IRON,
    GOLD;

    public Material getMaterial() {
        switch (this) {
            case BRICK:
                return Material.CLAY_BRICK;
            case IRON:
                return Material.IRON_INGOT;
            case GOLD:
                return Material.GOLD_INGOT;
            default:
                return null;
        }
    }
}
