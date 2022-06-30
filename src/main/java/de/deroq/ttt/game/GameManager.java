package de.deroq.ttt.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GameMap;
import de.deroq.ttt.timers.lobby.LobbyIdleTimer;
import de.deroq.ttt.timers.lobby.LobbyTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private final TTT ttt;
    private GameState gameState;
    private TimerTask currentTimer;
    private GameMap currentGameMap;
    private boolean forceStarted;
    private final Collection<GamePlayer> players;

    public GameManager(TTT ttt) {
        this.ttt = ttt;
        this.gameState = GameState.LOBBY;
        this.forceStarted = false;
        this.players = new ArrayList<>();

        initLobbyIdleTimer();
    }

    public void initLobbyTimer() {
        if(forceStarted) {
            return;
        }

        if(Bukkit.getOnlinePlayers().size() == Constants.NEEDED_PLAYERS) {
            setCurrentTimer(createLobbyTimer());
        }
    }


    public TimerTask createLobbyTimer() {
        LobbyTimer lobbyTimer = new LobbyTimer(ttt);
        lobbyTimer.onStart();
        return lobbyTimer;
    }

    private void initLobbyIdleTimer() {
        LobbyIdleTimer lobbyIdleTimer = new LobbyIdleTimer(ttt);
        lobbyIdleTimer.onStart();
    }

    public Inventory getRolePassInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8Rollenpass einlösen");
        inventory.setItem(2, Constants.DETECTIVE_PASS_ITEM);
        inventory.setItem(6, Constants.TRAITOR_PASS_ITEM);
        return inventory;
    }

    public void teleportToLobby(Player player) {
        Location lobbyLocation = ttt.getFileManager().getLocationsConfig().getLocation(Constants.LOBBY_LOCATION_NAME);
        if(lobbyLocation != null) {
            player.teleport(ttt.getFileManager().getLocationsConfig().getLocation(Constants.LOBBY_LOCATION_NAME));
        }
    }

    public void teleportToSpawns() {
        int spawnLocation = 0;
        List<String> spawnLocations = currentGameMap.getSpawnLocations();

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(BukkitUtils.locationFromString(spawnLocations.get(spawnLocation)));
            spawnLocation++;
        }
    }

    public Collection<GamePlayer> getAlive() {
        return players
                .stream()
                .filter(gamePlayer -> !gamePlayer.isSpectator())
                .collect(Collectors.toList());
    }

    public Collection<GamePlayer> getSpectators() {
        return players
                .stream()
                .filter(GamePlayer::isSpectator)
                .collect(Collectors.toList());
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public TimerTask getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(TimerTask currentTimer) {
        this.currentTimer = currentTimer;
    }

    public GameMap getCurrentGameMap() {
        return currentGameMap;
    }

    public void setCurrentGameMap(GameMap currentGameMap) {
        this.currentGameMap = currentGameMap;
    }

    public boolean isForceStarted() {
        return forceStarted;
    }

    public void setForceStarted(boolean forceStarted) {
        this.forceStarted = forceStarted;
    }

    public Collection<GamePlayer> getPlayers() {
        return players;
    }
}
