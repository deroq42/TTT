package de.deroq.ttt.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GameMap;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.timers.lobby.LobbyIdleTimer;
import de.deroq.ttt.timers.lobby.LobbyTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.timers.restart.RestartTimer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import de.deroq.ttt.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private final TTT ttt;
    private GameState gameState;
    private TimerTask currentTimer;
    private GameMap currentGameMap;
    private Collection<GamePlayer> players;
    private boolean forceStarted;
    private boolean enteredTraitorTester;
    private boolean triggeredTraitorTrap;

    public GameManager(TTT ttt) {
        this.ttt = ttt;
        this.gameState = GameState.LOBBY;
        this.players = new ArrayList<>();
        this.forceStarted = false;
        this.enteredTraitorTester = false;
        this.triggeredTraitorTrap = false;

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

    public void setSpectator(Player player) {
        PlayerUtils.loadPlayer(player);
        getGamePlayer(player.getUniqueId()).get().setSpectator(true, getAlive());
        player.teleport(BukkitUtils.locationFromString(currentGameMap.getSpectatorLocation()));
    }

    public void lootRandomWeapon(Player player, Block block) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId()).get();
        if (gamePlayer.isSpectator()) {
            return;
        }

        List<Material> items = Arrays.asList(
                Material.STONE_SWORD,
                Material.WOODEN_SWORD,
                Material.BOW);

        Collections.shuffle(items);
        for (Material material : items) {
            if (player.getInventory().contains(material)) {
                continue;
            }

            player.getInventory().addItem(new ItemStack(material));
            if (material == Material.BOW) {
                player.getInventory().addItem(new ItemStack(Material.ARROW, 32));
            }

            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 3f, 3f);
            block.setType(Material.AIR);
            break;
        }
    }

    public void lootIronSword(Player player, Block block) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId()).get();
        if (gamePlayer.isSpectator()) {
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 3f, 3f);
        block.setType(Material.AIR);
    }

    public void enterTraitorTester(Player player) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId()).get();
        if (gamePlayer.isSpectator()) {
            return;
        }

        if (enteredTraitorTester) {
            player.sendMessage(Constants.PREFIX + "Es ist bereits jemand im Traitor-Tester");
            return;
        }

        Role role = gamePlayer.getRole();
        if (role == Role.DETECTIVE) {
            player.sendMessage(Constants.PREFIX + "Du darfst den Traitor-Tester nicht betreten");
            return;
        }

        Location location = BukkitUtils.locationFromString(currentGameMap.getTesterLocation());
        player.teleport(location);
        BukkitUtils.sendBroadcastSoundInRadius(location, 7, 5, 7, Sound.BLOCK_PISTON_EXTEND);
        player.getNearbyEntities(2, 0, 2).forEach(entity -> entity.teleport(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-2))));
        BukkitUtils.sendBroadcastMessage("§3" + player.getName() + " §7hat den Traitor-Tester betreten");
        evaluateTraitorTesterResult(location, role);
        enteredTraitorTester = true;
    }

    private void evaluateTraitorTesterResult(Location location, Role role) {
        Location rightLight = BukkitUtils.locationFromString(currentGameMap.getRightTesterLightLocation());
        Location leftLight = BukkitUtils.locationFromString(currentGameMap.getLeftTesterLightLocation());

        Bukkit.getScheduler().runTaskLater(ttt, () -> {
            rightLight.getBlock().setType(role.getTesterLight());
            leftLight.getBlock().setType(role.getTesterLight());
            BukkitUtils.sendBroadcastSoundInRadius(location, 7, 5, 7, role.getTesterSound());
            enteredTraitorTester = false;

            Bukkit.getScheduler().runTaskLater(ttt, () -> {
                rightLight.getBlock().setType(Material.WHITE_STAINED_GLASS);
                leftLight.getBlock().setType(Material.WHITE_STAINED_GLASS);
            }, 3 * 20);
        }, 5 * 20);
    }

    public void triggerTraitorTrap(Player player) {
        GamePlayer gamePlayer = getGamePlayer(player.getUniqueId()).get();
        if (gamePlayer.isSpectator()) {
            return;
        }

        if (triggeredTraitorTrap) {
            player.sendMessage(Constants.PREFIX + "Die Traitor-Falle wurde bereits ausgelöst");
            return;
        }

        if (gamePlayer.getRole() != Role.TRAITOR) {
            player.sendMessage(Constants.PREFIX + "Nur Traitor können diese Falle auslösen");
            return;
        }

        Location location = BukkitUtils.locationFromString(currentGameMap.getTesterLocation()).subtract(0, 1, 0);
        List<BlockFace> blockFaces = Arrays.asList(
                BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST,
                BlockFace.WEST, BlockFace.SELF, BlockFace.EAST,
                BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST);

        player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 3f, 3f);
        BukkitUtils.sendBroadcastMessage("Die Traitor-Falle wurde ausgelöst");
        triggeredTraitorTrap = true;

        blockFaces
                .stream()
                .map(blockFace -> location.getBlock().getRelative(blockFace))
                .forEach(block -> {
                    block.setType(Material.AIR);
                    Bukkit.getScheduler().runTaskLater(ttt, () -> block.setType(Material.IRON_BLOCK), 4 * 20);
                });
    }

    public void checkForWin() {
        if (getTraitors().size() == getAlive().size() || getInnocents().size() + getDetectives().size() == getAlive().size()) {
            this.gameState = GameState.RESTART;
            Bukkit.getOnlinePlayers().forEach(this::teleportToLobby);

            Role role = getWinningTeam();
            BukkitUtils.sendBroadcastMessage("Die " + role.getColorCode() + role.getText() + " §7haben gewonnen");

            RestartTimer restartTimer = new RestartTimer(ttt);
            restartTimer.onStart();
            this.currentTimer = restartTimer;
        }
    }

    private Role getWinningTeam() {
        if(getTraitors().size() == getAlive().size()) {
            return Role.TRAITOR;
        }

        return Role.INNOCENT;
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

    public boolean isEnteredTraitorTester() {
        return enteredTraitorTester;
    }

    public void setEnteredTraitorTester(boolean enteredTraitorTester) {
        this.enteredTraitorTester = enteredTraitorTester;
    }

    public boolean isTriggeredTraitorTrap() {
        return triggeredTraitorTrap;
    }

    public void setTriggeredTraitorTrap(boolean triggeredTraitorTrap) {
        this.triggeredTraitorTrap = triggeredTraitorTrap;
    }

    public Collection<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<GamePlayer> players) {
        this.players = players;
    }
}

