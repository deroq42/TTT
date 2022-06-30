package de.deroq.ttt.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GameMap;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.timers.lobby.LobbyIdleTimer;
import de.deroq.ttt.timers.lobby.LobbyTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;
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
        if (forceStarted) {
            return;
        }

        if (Bukkit.getOnlinePlayers().size() == Constants.NEEDED_PLAYERS) {
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

    public void teleportToLobby(Player player) {
        Location lobbyLocation = ttt.getFileManager().getLocationsConfig().getLocation(Constants.LOBBY_LOCATION_NAME);
        if (lobbyLocation != null) {
            player.teleport(ttt.getFileManager().getLocationsConfig().getLocation(Constants.LOBBY_LOCATION_NAME));
        }
    }

    public void teleportToSpawns() {
        int spawnLocation = 0;
        List<String> spawnLocations = currentGameMap.getSpawnLocations();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(BukkitUtils.locationFromString(spawnLocations.get(spawnLocation)));
            spawnLocation++;
        }
    }

    public void triggerTraitorTrap() {
        Location location = BukkitUtils.locationFromString(currentGameMap.getTesterLocation()).subtract(0, 1, 0);
        List<BlockFace> blockFaces = Arrays.asList(
                BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST,
                BlockFace.WEST, BlockFace.SELF, BlockFace.EAST,
                BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST);

        blockFaces
                .stream()
                .map(blockFace -> location.getBlock().getRelative(blockFace))
                .forEach(block -> {
                    block.setType(Material.AIR);
                    Bukkit.getScheduler().runTaskLater(ttt, () -> block.setType(Material.IRON_BLOCK), 4 * 20);
                });
    }

    public void allocateRoles() {
        allocateTraitors();
        allocateDetectives();
        allocateInnocents();
    }

    private void allocateTraitors() {
        double traitorsValue = Bukkit.getOnlinePlayers().size() * Constants.TRAITOR_ALLOCATE_RATE;
        int traitors = (int) Math.round(traitorsValue);

        Random random = new Random();
        List<GamePlayer> players = getNoRoles();

        for (int i = 0; i < traitors; i++) {
            GamePlayer gamePlayer = players.get(random.nextInt(players.size()));
            gamePlayer.setRole(Role.TRAITOR);
            players.remove(gamePlayer);
        }
    }

    private void allocateDetectives() {
        double detectivesValue = Bukkit.getOnlinePlayers().size() * Constants.DETECTIVE_ALLOCATE_RATE;
        int detectives = (int) Math.round(detectivesValue);

        Random random = new Random();
        List<GamePlayer> players = getNoRoles();

        for (int i = 0; i < detectives; i++) {
            GamePlayer gamePlayer = players.get(random.nextInt(players.size()));
            gamePlayer.setRole(Role.DETECTIVE);
            players.remove(gamePlayer);
        }
    }

    private void allocateInnocents() {
        List<GamePlayer> players = getNoRoles();
        for (GamePlayer gamePlayer : players) {
            gamePlayer.setRole(Role.INNOCENT);
        }
    }

    public Optional<GamePlayer> getGamePlayer(UUID uuid) {
        return players
                .stream()
                .filter(gamePlayer -> gamePlayer.getUuid().equals(uuid))
                .findFirst();
    }

    public List<GamePlayer> getAlive() {
        return players
                .stream()
                .filter(gamePlayer -> !gamePlayer.isSpectator())
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getSpectators() {
        return players
                .stream()
                .filter(GamePlayer::isSpectator)
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getTraitors() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.TRAITOR)
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getDetectives() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.DETECTIVE)
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getInnocents() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.INNOCENT)
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getNoRoles() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == null)
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

