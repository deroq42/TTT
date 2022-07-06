package de.deroq.ttt.config.models;

import de.deroq.ttt.config.Config;

import java.io.File;

public class SettingsConfig extends Config {

    private String waitingLobbyLocation;
    private int minPlayers;
    private int maxPlayers;

    private SettingsConfig(File file) {
        super(file.getName());
    }

    public String getWaitingLobbyLocation() {
        return waitingLobbyLocation;
    }

    public void setWaitingLobbyLocation(String waitingLobbyLocation) {
        this.waitingLobbyLocation = waitingLobbyLocation;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public static SettingsConfig create(File file) {
        return new SettingsConfig(file);
    }
}
