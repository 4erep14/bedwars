package com.test.bedwars;

import com.test.bedwars.commands.Start;
import com.test.bedwars.commands.Vanish;
import com.test.bedwars.listeners.Build;
import com.test.bedwars.listeners.Damage;
import com.test.bedwars.listeners.Food;
import com.test.bedwars.listeners.Join;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    private GameState gameState;

    public List<Player> alive = new ArrayList<>();
    public List<Player> spectating = new ArrayList<>();
    public List<Player> vanished = new ArrayList<>();

    public GameState getGamestate() {return gameState; }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;

    }

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();
        setGameState(GameState.LOBBY);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }



    private void registerCommands() {
        getCommand("vanish").setExecutor(new Vanish(this));
        getCommand("start").setExecutor(new Start(this));
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Join(this), this);
        pm.registerEvents(new Build(this), this);
        pm.registerEvents(new Damage(this), this);
        pm.registerEvents(new Food(this), this);
    }
}