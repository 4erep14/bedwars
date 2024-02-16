package com.test.bedwars.gamemanager;

import com.test.bedwars.BedwarsPlugin;
import com.test.bedwars.config.ConfigurationManager;
import com.test.bedwars.db.MatchDataStorage;
import com.test.bedwars.gui.GUIManager;
import com.test.bedwars.players.PlayerManager;
import com.test.bedwars.scoreboards.PacketUtils;
import com.test.bedwars.setup.SetupWizardManager;
import com.test.bedwars.stats.Match;
import com.test.bedwars.worlds.GameStartCountdown;
import com.test.bedwars.worlds.GameWorld;
import com.test.bedwars.worlds.generators.GeneratorManager;
import com.test.bedwars.worlds.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class GameManager {
    private final BedwarsPlugin plugin;

    private SetupWizardManager setupWizardManager;
    private ConfigurationManager configurationManager;
    private GUIManager guiManager;
    private GeneratorManager generatorManager;
    private ShopManager shopManager;
    private PlayerManager playerManager;

    private GameWorld gameWorld;
    private Match match;

    private GameState state;

    public GameManager(BedwarsPlugin plugin) {
        this.plugin = plugin;

        this.setupWizardManager = new SetupWizardManager(this);
        this.configurationManager = new ConfigurationManager(this);
        this.guiManager = new GUIManager();
        this.configurationManager.loadWorld(this.configurationManager.randomMapName(), gameWorld -> {
            this.gameWorld = gameWorld;
        });
        this.generatorManager = new GeneratorManager(this);
        this.shopManager = new ShopManager(this);
        this.playerManager = new PlayerManager(this);
        this.match = new Match(this);
    }

    public void setState(GameState state) {
        this.state = state;

        switch(state) {
            case STARTING:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().clear();
                    player.teleport(gameWorld.getLobbyPosition());
                }
                new GameStartCountdown(this, 10).start();
                break;
            case ACTIVE:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    getPlayerManager().teleportToIslandSpawn(player);
                }
                break;
            case ENDGAME:
                for(Player player : Bukkit.getOnlinePlayers()) {
                   // if(getGameWorld().getActiveIslands().get(0).isMember(player)) {
                        PacketUtils.sendTitle(player, "You win!!!", 10, 70, 20);
                    //} else {
                      //  PacketUtils.sendTitle(player, "You lose", 10, 70, 20);
                    //}
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer("The game is over!");
                    }
                    MatchDataStorage.storeMatchData(match);
                    Bukkit.getServer().shutdown();
                }, 100);
                break;
            case RESET:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer("Restarting Game!");
                }

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    gameWorld.resetWorld();
                    Bukkit.getServer().shutdown();
                }, 20);
                break;
        }
    }

    public GameState getState() { return state; }

    public BedwarsPlugin getPlugin() {
        return this.plugin;
    }

    public SetupWizardManager getSetupWizardManager() {
        return setupWizardManager;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public GUIManager getGuiManager() { return guiManager; }

      public GameWorld getGameWorld() { return gameWorld; }

    public GeneratorManager getGeneratorManager() { return generatorManager; }

    public ShopManager getShopManager() { return shopManager; }

    public PlayerManager getPlayerManager() { return playerManager; }

    public Match getMatch() { return match; }
}
