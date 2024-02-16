package com.test.bedwars.worlds.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class Generator {

    private Location location;
    private GeneratorType type;
    private long interval;

    public Generator(Location location, GeneratorType type) {
        this.location = location;
        this.type = type;
        switch (type) {
            case BRICK:
                interval = 1;
                break;
            case IRON:
                interval = 5;
                break;
            case GOLD:
                interval = 30;
                break;
        }
    }

    public void spawn() {
        Item item = (Item) location.getWorld().spawnEntity(location, EntityType.DROPPED_ITEM);

        switch (type) {
            case IRON:
                item.setItemStack(new ItemStack(Material.IRON_INGOT));
                break;
            case GOLD:
                item.setItemStack(new ItemStack(Material.GOLD_INGOT));
                break;
        }
    }

    public Location getLocation() {
        return location;
    }

    public GeneratorType getType() {
        return type;
    }

    public long getInterval() {
        return interval;
    }
}
