package com.test.bedwars;

import com.test.bedwars.commands.ResetCommand;
import com.test.bedwars.commands.SetupWizardCommand;
import com.test.bedwars.commands.StartCommand;
import com.test.bedwars.events.*;
import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        gameManager.setState(GameState.LOBBY);

        getServer().getPluginManager().registerEvents(new PlayerLoginEventListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new PlayerItemInteractListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new BlockUpdateListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new ShopInteractListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new PlayerIngameEventListener(gameManager), this);

        getCommand("setup").setExecutor(new SetupWizardCommand(gameManager));
        getCommand("start").setExecutor(new StartCommand(gameManager));
        getCommand("reset").setExecutor(new ResetCommand(gameManager));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}