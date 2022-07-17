package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.GameState;
import de.deroq.ttt.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final TTT ttt;

    public PlayerJoinListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        PlayerUtils.loadPlayer(player);
        PlayerUtils.loadInventory(player, ttt.getGameManager().getGameState());

        GamePlayer gamePlayer = GamePlayer.create(player.getUniqueId());

        if (ttt.getGameManager().getGameState() == GameState.LOBBY) {
            BukkitUtils.sendBroadcastMessage("§3" + player.getName() + " §7hat die Runde betreten " + BukkitUtils.getOnlinePlayers(ttt.getGameManager().MAX_PLAYERS));
            ttt.getGameManager().teleportToLobby(player);
            ttt.getGameManager().initLobbyTimer();
            ttt.getGameManager().setLobbyScoreboard(gamePlayer);
        } else {
            if (ttt.getGameManager().getGameState() == GameState.PROTECTION) {
                ttt.getGameManager().setProtectionScoreboard(gamePlayer);
            } else {
                ttt.getGameManager().setIngameScoreboard(gamePlayer);
            }

            ttt.getGameManager().setSpectator(gamePlayer, true);
        }

        ttt.getGameManager().getGamePlayers().add(gamePlayer);
        ttt.getGameManager().updateTablist();
    }
}
