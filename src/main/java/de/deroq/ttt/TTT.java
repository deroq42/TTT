package de.deroq.ttt;

import de.deroq.ttt.commands.StartCommand;
import de.deroq.ttt.listeners.PlayerJoinListener;
import de.deroq.ttt.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TTT extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onEnable() {
        initManagers();
        registerCommands();
        registerListeners();

        getLogger().info("TTT has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TTT has been disabled.");
    }

    private void initManagers() {
        this.gameManager = new GameManager();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
    }

    private void registerCommands() {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        commandMap.register("start", new StartCommand("start", this));
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
