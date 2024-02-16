package com.test.bedwars.commands;

import com.test.bedwars.gamemanager.GameManager;
import com.test.bedwars.gamemanager.GameState;
import com.test.bedwars.worlds.GameWorld;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupWizardCommand implements CommandExecutor {

    private GameManager gameManager;

    public SetupWizardCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if(!player.hasPermission("bedwars.admin")) {
            player.sendMessage("You don't have permission to run this command ");
            return true; }

        if(args.length < 1) {
            player.sendMessage("/setup <map name>");
            return true;
        }

        String mapName = args[0];

        if(mapName.equalsIgnoreCase("exit")) {
            gameManager.getSetupWizardManager().removeFromWizard(player);

            return true;
        }

        player.sendMessage("load world, one moment... ");

        GameWorld world = new GameWorld(mapName);
        world.loadWorld(gameManager, false, () -> gameManager.getSetupWizardManager().activatedSetupWizard(player, world));
        return false;
    }
}
