package com.test.bedwars.commands;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {

    private GameManager gameManager;

    public StartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("bedwars.admin")) {
            gameManager.setState(GameState.STARTING);
        } else {
            commandSender.sendMessage("You don't have permission to run this command.");
        }
        return false;
    }
}
