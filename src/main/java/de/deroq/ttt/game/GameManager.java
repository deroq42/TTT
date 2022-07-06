package de.deroq.ttt.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.map.models.GameMap;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.timers.lobby.LobbyIdleTimer;
import de.deroq.ttt.timers.lobby.LobbyTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.timers.restart.RestartTimer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
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
    private Collection<GamePlayer> gamePlayers;
    private boolean forceStarted;
    private boolean enteredTraitorTester;
    private boolean triggeredTraitorTrap;
    private final int MIN_PLAYERS;

    /**
     * Constructor of the class.
     */
    public GameManager(TTT ttt) {
        this.ttt = ttt;
        this.gameState = GameState.LOBBY;
        this.gamePlayers = new ArrayList<>();
        this.forceStarted = false;
        this.enteredTraitorTester = false;
        this.triggeredTraitorTrap = false;
        this.MIN_PLAYERS = ttt.getFileManager().getSettingsConfig().getMinPlayers();

        initLobbyIdleTimer();
    }

    /**
     * Starts the lobby timer when enough players are online.
     */
    public void initLobbyTimer() {
        if (forceStarted) {
            return;
        }

        if (Bukkit.getOnlinePlayers().size() == MIN_PLAYERS) {
            setCurrentTimer(createLobbyTimer());
        }
    }

    /**
     * @return a new LobbyTimer.
     */
    public TimerTask createLobbyTimer() {
        LobbyTimer lobbyTimer = new LobbyTimer(ttt);
        lobbyTimer.onStart();
        return lobbyTimer;
    }

    /**
     * Starts the lobby idle timer when too few players are online.
     */
    private void initLobbyIdleTimer() {
        LobbyIdleTimer lobbyIdleTimer = new LobbyIdleTimer(ttt);
        lobbyIdleTimer.onStart();
    }

    /**
     * Teleports a player to the lobby location.
     *
     * @param player The player to teleport.
     */
    public void teleportToLobby(Player player) {
        String lobbyLocation = ttt.getFileManager().getSettingsConfig().getWaitingLobbyLocation();
        if (lobbyLocation != null) {
            player.teleport(BukkitUtils.locationFromString(lobbyLocation));
        }
    }

    /**
     * Teleports all players to the spawn locations on the current map.
     */
    public void teleportToSpawns() {
        int spawnLocation = 0;
        List<String> spawnLocations = currentGameMap.getSpawnLocations();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(BukkitUtils.locationFromString(spawnLocations.get(spawnLocation)));
            spawnLocation++;
        }
    }

    /**
     * Sets a player into the spectator mode.
     *
     * @param player The player who is to spectate.
     */
    public void setSpectator(Player player) {
        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        optionalGamePlayer.get().setSpectator(true, getAlive());
        player.teleport(BukkitUtils.locationFromString(currentGameMap.getSpectatorLocation()));
    }

    /**
     * Loots a random item from the chest.
     *
     * @param player The player who loots the item.
     * @param block The clicked chest.
     */
    public void lootRandomWeapon(Player player, Block block) {
        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
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

    /**
     * Loots an iron sword from the chest.
     *
     * @param player The player who loots the sword.
     * @param block The clicked chest.
     */
    public void lootIronSword(Player player, Block block) {
        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();

        if (gamePlayer.isSpectator()) {
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 3f, 3f);
        block.setType(Material.AIR);
    }

    /**
     * Enters into the traitor-tester.
     *
     * @param player The player who enters.
     */
    public void enterTraitorTester(Player player) {
        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
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

    /**
     * Evaluates the traitor-tester.
     *
     * @param location The location of the traitor-tester
     * @param role The role of the player who entered the traitor-tester.
     */
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

    /**
     * Triggers the traitor trap.
     *
     * @param player The player who triggers the trap.
     */
    public void triggerTraitorTrap(Player player) {
        Optional<GamePlayer> optionalGamePlayer = getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
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

    /**
     * Checks for the win.
     */
    public void checkForWin() {
        if (getTraitors().size() == getAlive().size() || getInnocents().size() + getDetectives().size() == getAlive().size()) {
            this.gameState = GameState.RESTART;
            Bukkit.getOnlinePlayers().forEach(this::teleportToLobby);

            Role role = getWinningTeam();
            BukkitUtils.sendBroadcastMessage("Die " + role.getColorCode() + role.getName() + " §7haben gewonnen");

            RestartTimer restartTimer = new RestartTimer(ttt);
            restartTimer.onStart();
            this.currentTimer = restartTimer;
        }
    }

    /**
     * Gets the team who won the game.
     *
     * @return the role of the winning team.
     */
    private Role getWinningTeam() {
        if(getTraitors().size() == getAlive().size()) {
            return Role.TRAITOR;
        }

        return Role.INNOCENT;
    }

    /**
     * Allocates all players to the roles.
     */
    public void allocateRoles() {
        allocateTraitors();
        allocateDetectives();
        allocateInnocents();
    }

    /**
     * Allocates random players to the traitor role by the guide value 0.34.
     */
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

    /**
     * Allocates random players to the detective role by the guide value 0.15.
     */
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

    /**
     * Allocates all players without a role to the innocent role.
     */
    private void allocateInnocents() {
        List<GamePlayer> players = getNoRoles();
        for (GamePlayer gamePlayer : players) {
            gamePlayer.setRole(Role.INNOCENT);
        }
    }

    /**
     * Get a GamePlayer by its uuid.
     *
     * @param uuid The uuid of the GamePlayer.
     * @return an Optional of a GamePlayer.
     */
    public Optional<GamePlayer> getGamePlayer(UUID uuid) {
        return gamePlayers
                .stream()
                .filter(gamePlayer -> gamePlayer.getUuid().equals(uuid))
                .findFirst();
    }

    /**
     * @return a List of GamePlayers who are still alive.
     */
    public List<GamePlayer> getAlive() {
        return gamePlayers
                .stream()
                .filter(gamePlayer -> !gamePlayer.isSpectator())
                .collect(Collectors.toList());
    }

    /**
     * @return a List of GamePlayers who got the traitor role.
     */
    public List<GamePlayer> getTraitors() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.TRAITOR)
                .collect(Collectors.toList());
    }

    /**
     * @return a List of GamePlayers who got the detective role.
     */
    public List<GamePlayer> getDetectives() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.DETECTIVE)
                .collect(Collectors.toList());
    }

    /**
     * @return a List of GamePlayers who got the innocent role.
     */
    public List<GamePlayer> getInnocents() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == Role.INNOCENT)
                .collect(Collectors.toList());
    }

    /**
     * @return a List of GamePlayers who got no role.
     */
    public List<GamePlayer> getNoRoles() {
        return getAlive()
                .stream()
                .filter(gamePlayer -> gamePlayer.getRole() == null)
                .collect(Collectors.toList());
    }

    /**
     * @return the current state.
     */
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * @return the current timer.
     */
    public TimerTask getCurrentTimer() {
        return currentTimer;
    }

    public void setCurrentTimer(TimerTask currentTimer) {
        this.currentTimer = currentTimer;
    }

    /**
     * @return the current map on which the game is played.
     */
    public GameMap getCurrentGameMap() {
        return currentGameMap;
    }

    public void setCurrentGameMap(GameMap currentGameMap) {
        this.currentGameMap = currentGameMap;
    }

    /**
     * @return true if the game has been started by command.
     */
    public boolean isForceStarted() {
        return forceStarted;
    }

    public void setForceStarted(boolean forceStarted) {
        this.forceStarted = forceStarted;
    }

    /**
     * @return true if currently one is in the traitor-tester.
     */
    public boolean isEnteredTraitorTester() {
        return enteredTraitorTester;
    }

    public void setEnteredTraitorTester(boolean enteredTraitorTester) {
        this.enteredTraitorTester = enteredTraitorTester;
    }

    /**
     * @return true if the traitor trap has already been triggered.
     */
    public boolean isTriggeredTraitorTrap() {
        return triggeredTraitorTrap;
    }

    public void setTriggeredTraitorTrap(boolean triggeredTraitorTrap) {
        this.triggeredTraitorTrap = triggeredTraitorTrap;
    }

    /**
     * @return a Collection of GamePlayers.
     */
    public Collection<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Collection<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}

