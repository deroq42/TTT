package de.deroq.ttt;

import de.deroq.ttt.commands.ForceMapCommand;
import de.deroq.ttt.commands.map.*;
import de.deroq.ttt.commands.SetLobbyCommand;
import de.deroq.ttt.commands.StartCommand;
import de.deroq.ttt.database.TTTDatabase;
import de.deroq.ttt.database.misc.TTTDatabaseBuilder;
import de.deroq.ttt.managers.GameMapManager;
import de.deroq.ttt.listeners.PlayerInteractListener;
import de.deroq.ttt.listeners.PlayerJoinListener;
import de.deroq.ttt.managers.FileManager;
import de.deroq.ttt.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TTT extends JavaPlugin {

    private TTTDatabase tttDatabase;
    private FileManager fileManager;
    private GameManager gameManager;
    private GameMapManager gameMapManager;

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
        this.tttDatabase = new TTTDatabaseBuilder(this)
                .setHost("localhost")
                .setUsername("root")
                .setDatabase("ttt")
                .setPassword("123456")
                .setPort(27017)
                .build();

        tttDatabase.connect();
    }

    private void initManagers() {
        this.fileManager = new FileManager();
        fileManager.loadFiles();

        this.gameManager = new GameManager(this);
        this.gameMapManager = new GameMapManager(this);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
    }

    private void registerCommands() {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        commandMap.register("start", new StartCommand("start", this));
        commandMap.register("forcemap", new ForceMapCommand("forcemap", this));
        commandMap.register("setLobby", new SetLobbyCommand("setLobby", this));
        commandMap.register("createMap", new CreateMapCommand("createMap", this));
        commandMap.register("addSpawn", new AddSpawnCommand("addSpawn", this));
        commandMap.register("setSpectator", new SetSpectatorCommand("setSpectator", this));
        commandMap.register("setTester", new SetTesterCommand("setTester", this));
        commandMap.register("addTesterLight", new AddTesterLightCommand("addTesterLight", this));
        commandMap.register("addBuilder", new AddBuilderCommand("addBuilder", this));
    }

    public TTTDatabase getTTTDatabase() {
        return tttDatabase;
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
}
