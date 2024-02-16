package com.test.bedwars.worlds.shop;

import com.test.bedwars.worlds.generators.GeneratorType;
import org.bukkit.Material;

public class ShopItem {

    private String name;
    private Material material;
    private int price;
    private GeneratorType currency;

    public ShopItem(String name, Material material, int price, GeneratorType currency) {
        this.name = name;
        this.material = material;
        this.price = price;
        this.currency = currency;
    }
    public String getName() { return name; }

    public Material getMaterial() { return material; }

    public int getPrice() { return price; }

    public GeneratorType getCurrency() { return currency; }

}
