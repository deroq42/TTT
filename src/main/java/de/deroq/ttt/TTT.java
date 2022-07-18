package de.deroq.ttt;

import de.deroq.database.models.DatabaseService;
import de.deroq.database.models.DatabaseServiceType;
import de.deroq.database.services.mongo.MongoDatabaseService;
import de.deroq.ttt.commands.game.ForceMapCommand;
import de.deroq.ttt.commands.game.ShopCommand;
import de.deroq.ttt.commands.game.StartCommand;
import de.deroq.ttt.commands.map.*;
import de.deroq.ttt.commands.misc.SetLobbyCommand;
import de.deroq.ttt.commands.misc.SetMaxPlayersCommand;
import de.deroq.ttt.commands.misc.SetMinPlayersCommand;
import de.deroq.ttt.game.map.GameMapManager;
import de.deroq.ttt.game.shop.GameShopManager;
import de.deroq.ttt.listeners.*;
import de.deroq.ttt.config.FileManager;
import de.deroq.ttt.game.GameManager;
import de.deroq.ttt.listeners.ttt.TTTDropOutListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TTT extends JavaPlugin {

    private MongoDatabaseService databaseService;
    private FileManager fileManager;
    private GameManager gameManager;
    private GameMapManager gameMapManager;
    private GameShopManager gameShopManager;

    @Override
    public void onEnable() {
        initDatabase();
        initManagers();
        registerCommands();
        registerListeners();

        getLogger().info("TTT has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TTT has been disabled.");
    }

    private void initDatabase() {
        this.databaseService = (MongoDatabaseService) new DatabaseService.builder(DatabaseServiceType.MONGO)
                .setHost("localhost")
                .setUsername("root")
                .setDatabase("ttt")
                .setPassword("123456")
                .setPort(27017)
                .build();

        databaseService.connect();
    }

    private void initManagers() {
        this.fileManager = new FileManager();
        fileManager.loadFiles();

        this.gameManager = new GameManager(this);
        this.gameMapManager = new GameMapManager(this);
        this.gameShopManager = new GameShopManager(this);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new FoodLevelChangeListener(), this);
        pluginManager.registerEvents(new EntityDamageListener(this), this);
        pluginManager.registerEvents(new EntityDamageByEntityListener(this), this);
        pluginManager.registerEvents(new PlayerDeathListener(this), this);
        pluginManager.registerEvents(new InventoryClickListener(this), this);

        /* TTT */
        pluginManager.registerEvents(new TTTDropOutListener(this), this);
    }

    private void registerCommands() {
        /* GAME */
        getCommand("start").setExecutor(new StartCommand(this));
        getCommand("forcemap").setExecutor(new ForceMapCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));

       /* MAP */
        getCommand("createMap").setExecutor(new CreateMapCommand(this));
        getCommand("deleteMap").setExecutor(new DeleteMapCommand(this));
        getCommand("addSpawn").setExecutor(new AddSpawnCommand(this));
        getCommand("setSpectator").setExecutor(new SetSpectatorCommand(this));
        getCommand("setTester").setExecutor(new SetTesterCommand(this));
        getCommand("setTesterLight").setExecutor(new SetTesterLightCommand(this));
        getCommand("addBuilder").setExecutor(new AddBuilderCommand(this));

        /* MISC */
        getCommand("setMaxPlayers").setExecutor(new SetMaxPlayersCommand(this));
        getCommand("setMinPlayers").setExecutor(new SetMinPlayersCommand(this));
        getCommand("setLobby").setExecutor(new SetLobbyCommand(this));
    }

    public MongoDatabaseService getDatabaseService() {
        return databaseService;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameMapManager getGameMapManager() {
        return gameMapManager;
    }

    public GameShopManager getGameShopManager() {
        return gameShopManager;
    }
}
