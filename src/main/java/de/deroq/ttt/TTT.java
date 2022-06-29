package de.deroq.ttt;

import de.deroq.ttt.commands.SetLobbyCommand;
import de.deroq.ttt.commands.StartCommand;
import de.deroq.ttt.countdowns.LobbyIdleTimer;
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

    private FileManager fileManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        initManagers();
        registerCommands();
        registerListeners();
        initLobbyIdleTimer();

        getLogger().info("TTT has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TTT has been disabled.");
    }

    private void initManagers() {
        this.fileManager = new FileManager();
        fileManager.loadFiles();

        this.gameManager = new GameManager(this);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
    }

    private void registerCommands() {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        commandMap.register("start", new StartCommand("start", this));
        commandMap.register("setLobby", new SetLobbyCommand("setLobby", this));
    }

    private void initLobbyIdleTimer() {
        LobbyIdleTimer lobbyIdleTimer = new LobbyIdleTimer(this);
        lobbyIdleTimer.onStart();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
