package com.test.bedwars.config;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.worlds.TeamColor;
import com.test.bedwars.worlds.GameWorld;
import com.test.bedwars.worlds.Island;
import com.test.bedwars.worlds.generators.Generator;
import com.test.bedwars.worlds.generators.GeneratorType;
import com.test.bedwars.worlds.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigurationManager {

    private GameManager gameManager;

    private ConfigurationSection mapsConfiguration;

    public ConfigurationManager(GameManager gameManager) {
        this.gameManager = gameManager;

        FileConfiguration fileConfiguration = gameManager.getPlugin().getConfig();
        mapsConfiguration = !fileConfiguration.isConfigurationSection("maps") ? fileConfiguration.createSection("maps") :
                fileConfiguration.getConfigurationSection("maps");
        gameManager.getPlugin().saveConfig();
    }

    public String randomMapName() {
        String[] mapNames =  mapsConfiguration.getKeys(false).toArray(new String[]{});
        return mapNames[ThreadLocalRandom.current().nextInt(mapNames.length)];
    }

    public void loadWorld(String mapName, Consumer<GameWorld> consumer) {
        GameWorld gameWorld = new GameWorld(mapName);
        gameWorld.loadWorld(gameManager, true, () -> {
            ConfigurationSection mapSection = getMapSection(mapName);
            for(String key : mapSection.getKeys(false)) {
                if(Arrays.stream(TeamColor.values()).anyMatch(color -> color.name().equals(key))) {
                    Island island = loadIsland(gameWorld, mapSection.getConfigurationSection(key));
                    gameWorld.addIsland(island);
                }

                gameWorld.setGenerators(loadGenerators(gameWorld, mapSection.getConfigurationSection("generators")));
                gameWorld.setShopItems(loadShop(gameWorld, gameManager.getPlugin().getConfig().getConfigurationSection("shops.islandShop.items")));

                gameWorld.setLobbySpawn(new Location(gameWorld.getWorld(), 0, 64, 0));

                consumer.accept(gameWorld);
            }
        });
    }

    public List<Generator> loadGenerators(GameWorld world, ConfigurationSection section) {
        return section.getKeys(false).stream().map(key -> {
            ConfigurationSection generatorSection = section.getConfigurationSection(key);
            Location location = locationFrom(world.getWorld(), generatorSection.getConfigurationSection("location"));
            String typeString = generatorSection.getString("type");

            if(Arrays.stream(GeneratorType.values()).noneMatch(type -> type.name().equals(typeString))) return null;

            GeneratorType type = GeneratorType.valueOf(typeString);
            return new Generator(location, type);
        }).collect(Collectors.toList());
    }

    public Island loadIsland(GameWorld world, ConfigurationSection section) {
        TeamColor color = TeamColor.valueOf(section.getName());
        Island island = new Island(world, color);

        try {
            island.setBedLocation(locationFrom(world.getWorld(), section.getConfigurationSection("bed")));
            island.setSpawnLocation(locationFrom(world.getWorld(), section.getConfigurationSection("spawn")));
            island.setShopEntityLocation(locationFrom(world.getWorld(), section.getConfigurationSection("shopEntity")));
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe("Invalid " + color.formattedName() +" island in " + world.getConfigName());
        }

        Villager shopEntity = (Villager) gameManager.getGameWorld().getWorld().spawnEntity(island.getShopEntityLocation(), EntityType.VILLAGER);
        shopEntity.setCustomName("Shop");
        shopEntity.setCustomNameVisible(true);
        shopEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 500));

        return island;
    }

    public List<ShopItem> loadShop(GameWorld world, ConfigurationSection section) {
        return section.getKeys(false).stream().map(key -> {

            ConfigurationSection shopItemSection = section.getConfigurationSection(key);
            String name = shopItemSection.getString("displayName");
            String materialName = shopItemSection.getString("material");
            int price = shopItemSection.getInt("price");
            String currencyName = shopItemSection.getString("currency");

            if(Arrays.stream(GeneratorType.values()).noneMatch(type -> type.name().equals(currencyName))) return null;
            if(Arrays.stream(Material.values()).noneMatch(material -> material.name().equals(materialName))) return null;

            Material material = Material.valueOf(materialName);
            GeneratorType currency = GeneratorType.valueOf(currencyName);
            return new ShopItem(name, material, price, currency);
        }).collect(Collectors.toList());
    }

    public ConfigurationSection getMapSection(String mapName) {
        if(!mapsConfiguration.isConfigurationSection(mapName))  mapsConfiguration.createSection(mapName);

        return mapsConfiguration.getConfigurationSection(mapName);
    }

    public void saveGenerator(String worldConfigName, Generator generator) {
        ConfigurationSection mapSection = getMapSection(worldConfigName);
        ConfigurationSection generatorSection;

        generatorSection = mapSection.isConfigurationSection("generators") ? mapSection.getConfigurationSection("generators")
                : mapSection.createSection("generators");

        ConfigurationSection section = generatorSection .createSection(UUID.randomUUID().toString());
        section.set("type", generator.getType().toString());
        writeLocation(generator.getLocation(), section.createSection("location"));

        gameManager.getPlugin().saveConfig();
    }

    public void saveIsland(Island island) {
        ConfigurationSection mapSection = getMapSection(island.getGameWorld().getConfigName());

        ConfigurationSection colorSection;
        colorSection = mapSection.isConfigurationSection(island.getColor().toString()) ? mapSection.getConfigurationSection(island.getColor().toString())
                : mapSection.createSection(island.getColor().toString());

        Map<String, Location> locationsToWrite = new HashMap<>();

        locationsToWrite.put("shopEntity", island.getShopEntityLocation());
        locationsToWrite.put("bed", island.getBedLocation());
        locationsToWrite.put("spawn", island.getSpawnLocation());

        for(Map.Entry<String, Location> entry : locationsToWrite.entrySet()) {
            ConfigurationSection section;
            section = !colorSection.isConfigurationSection(entry.getKey()) ? colorSection.createSection(entry.getKey())
                    : colorSection.getConfigurationSection(entry.getKey());

            if(entry.getValue() != null) writeLocation(entry.getValue(), section);
        }

        gameManager.getPlugin().saveConfig();
    }

    public void writeLocation(Location location, ConfigurationSection section) {
        section.set("x", location.getX());
        section.set("y", location.getY ());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    private Location locationFrom(World world, ConfigurationSection section) {
        return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }
}
