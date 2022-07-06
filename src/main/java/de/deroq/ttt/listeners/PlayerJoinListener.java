package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import de.deroq.ttt.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TTT ttt;
    private final int MIN_PLAYERS;

    public PlayerJoinListener(TTT ttt) {
        this.ttt = ttt;
        this.MIN_PLAYERS = ttt.getFileManager().getSettingsConfig().getMinPlayers();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        PlayerUtils.loadPlayer(player);
        PlayerUtils.loadInventory(player, ttt.getGameManager().getGameState());

        GamePlayer gamePlayer = GamePlayer.create(player.getUniqueId());

        if(ttt.getGameManager().getGameState() == GameState.LOBBY) {
            BukkitUtils.sendBroadcastMessage("§3" + player.getName() + " §7hat die Runde betreten " + BukkitUtils.getOnlinePlayers(MIN_PLAYERS));
            ttt.getGameManager().teleportToLobby(player);
            ttt.getGameManager().initLobbyTimer();
        }

        if(ttt.getGameManager().getGameState() != GameState.LOBBY) {
            ttt.getGameManager().setSpectator(player);
        }

        ttt.getGameManager().getGamePlayers().add(gamePlayer);
    }
}
