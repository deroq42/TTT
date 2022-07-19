package de.deroq.ttt.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.map.models.GameMap;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.game.scoreboard.ingame.IngameScoreboard;
import de.deroq.ttt.game.scoreboard.ingame.ProtectionScoreboard;
import de.deroq.ttt.game.scoreboard.lobby.LobbyScoreboard;
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
    private Collection<GamePlayer> gamePlayers;
    private boolean forceStarted;
    private boolean enteredTraitorTester;
    private boolean triggeredTraitorTrap;

    public final Location LOBBY_LOCATION;
    public final int MIN_PLAYERS;
    public final int MAX_PLAYERS;
    public final List<Material> LOOTABLE_ITEMS;

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

        this.LOBBY_LOCATION = BukkitUtils.locationFromString(ttt.getFileManager().getSettingsConfig().getWaitingLobbyLocation());
        this.MIN_PLAYERS = ttt.getFileManager().getSettingsConfig().getMinPlayers();
        this.MAX_PLAYERS = ttt.getFileManager().getSettingsConfig().getMaxPlayers();
        this.LOOTABLE_ITEMS = Arrays.asList(
                Material.STONE_SWORD,
                Material.WOODEN_SWORD,
                Material.BOW);

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
     * Starts the restart timer.
     */
    private void initRestartTimer() {
        currentTimer.onStop();

        RestartTimer restartTimer = new RestartTimer(ttt);
        restartTimer.onStart();
        this.currentTimer = restartTimer;
    }

    public void setLobbyScoreboard(GamePlayer gamePlayer) {
        LobbyScoreboard lobbyScoreboard = new LobbyScoreboard(ttt, gamePlayer);
        lobbyScoreboard.setScoreboard();
        lobbyScoreboard.setTablist();
    }

    public void setProtectionScoreboard(GamePlayer gamePlayer) {
        ProtectionScoreboard protectionScoreboard = new ProtectionScoreboard(ttt, gamePlayer);
        protectionScoreboard.setScoreboard();
        protectionScoreboard.setTablist();
    }

    public void setIngameScoreboard(GamePlayer gamePlayer) {
        IngameScoreboard ingameScoreboard = new IngameScoreboard(ttt, gamePlayer);
        ingameScoreboard.setScoreboard();
        ingameScoreboard.setTablist();
    }

    public void updateScoreboard() {
        getGamePlayers().forEach(gamePlayer -> gamePlayer.getGameScoreboard().updateScoreboard());
    }

    public void updateTablist() {
        getGamePlayers().forEach(gamePlayer -> gamePlayer.getGameScoreboard().updateTablist());
    }

    /**
     * Teleports a player to the lobby location.
     *
     * @param player The player to teleport.
     */
    public void teleportToLobby(Player player) {
        if (player.isDead()) {
            player.spigot().respawn();
        }

        if (LOBBY_LOCATION != null) {
            player.teleport(LOBBY_LOCATION);
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
     * @param gamePlayer The GamePlayer who is to spectate.
     */
    public void setSpectator(GamePlayer gamePlayer, boolean spectator) {
        Player player = gamePlayer.getPlayer();
        gamePlayer.setSpectator(spectator, getAlive());

        if (spectator) {
            if(player.isDead()) {
                player.spigot().respawn();
            }

            gamePlayer.getPlayer().teleport(BukkitUtils.locationFromString(currentGameMap.getSpectatorLocation()));
        }
    }

    /**
     * Loots a random item from the chest.
     *
     * @param gamePlayer The GamePlayer who loots the item.
     * @param block      The clicked chest.
     */
    public void lootRandomWeapon(GamePlayer gamePlayer, Block block) {
        Player player = gamePlayer.getPlayer();
        List<Material> items = new ArrayList<>(LOOTABLE_ITEMS);

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
     * @param gamePlayer The GamePlayer who loots the sword.
     * @param block      The clicked chest.
     */
    public void lootIronSword(GamePlayer gamePlayer, Block block) {
        Player player = gamePlayer.getPlayer();
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
     * @param gamePlayer The GamePlayer who enters.
     */
    public void enterTraitorTester(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

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
        player.getNearbyEntities(2, 0, 2).forEach(entity -> entity.teleport(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(-2))));

        BukkitUtils.sendBroadcastMessage("§3" + player.getName() + " §7hat den Traitor-Tester betreten");
        BukkitUtils.sendBroadcastSoundInRadius(location, 7, 5, 7, Sound.BLOCK_PISTON_EXTEND);
        enteredTraitorTester = true;

        evaluateTraitorTesterResult(gamePlayer, location, role);
    }

    /**
     * Evaluates the traitor-tester.
     *
     * @param location The location of the traitor-tester
     * @param role     The role of the player who entered the traitor-tester.
     */
    private void evaluateTraitorTesterResult(GamePlayer gamePlayer, Location location, Role role) {
        if(role == Role.TRAITOR && gamePlayer.isInnocentTicket()) {
            role = Role.INNOCENT;
        }

        final Role evaluatedRole = role;
        Location rightLight = BukkitUtils.locationFromString(currentGameMap.getRightTesterLightLocation());
        Location leftLight = BukkitUtils.locationFromString(currentGameMap.getLeftTesterLightLocation());

        Bukkit.getScheduler().runTaskLater(ttt, () -> {
            rightLight.getBlock().setType(evaluatedRole.getTesterLight());
            leftLight.getBlock().setType(evaluatedRole.getTesterLight());
            BukkitUtils.sendBroadcastSoundInRadius(location, 7, 5, 7, evaluatedRole.getTesterSound());
            enteredTraitorTester = false;

            Bukkit.getScheduler().runTaskLater(ttt, () -> {
                rightLight.getBlock().setType(Material.WHITE_STAINED_GLASS);
                leftLight.getBlock().setType(Material.WHITE_STAINED_GLASS);
                gamePlayer.setInnocentTicket(false);
            }, 3 * 20);
        }, 5 * 20);
    }

    /**
     * Triggers the traitor trap.
     *
     * @param gamePlayer The GamePlayer who triggers the trap.
     */
    public void triggerTraitorTrap(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

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
     * Checks for a win.
     *
     * @return null if there is no winner yet.
     */
    public Role checkForWin() {
        if (getTraitors().size() == getAlive().size()) {
            return Role.TRAITOR;

        } else if (getInnocents().size() + getDetectives().size() == getAlive().size()) {
            return Role.INNOCENT;
        }

        return null;
    }

    /**
     * Triggers on game win.
     *
     * @param role The winning role.
     */
    public void onWin(Role role) {
        getGamePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            teleportToLobby(player);
            setSpectator(gamePlayer, false);
            PlayerUtils.loadPlayer(player);
        });

        StringBuilder traitorBuilder = new StringBuilder();
        getTraitors().forEach(gamePlayer -> traitorBuilder.append(gamePlayer.getPlayer().getName()).append(" "));

        BukkitUtils.sendBroadcastMessage("Die " + role.getColorCode() + role.getName() + " §7haben gewonnen");
        BukkitUtils.sendBroadcastMessage("Die Traitor waren: §4" + traitorBuilder);
        BukkitUtils.spawnFirework(LOBBY_LOCATION);
        this.gameState = GameState.RESTART;
        initRestartTimer();
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

    public List<GamePlayer> getSpectators() {
        return gamePlayers
                .stream()
                .filter(GamePlayer::isSpectator)
                .collect(Collectors.toList());
    }

    public void setGamePlayers(Collection<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
}

