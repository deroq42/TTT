package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

/**
 * @author deroq
 * @since 09.07.2022
 */

public class AsyncPlayerChatListener implements Listener {

    private final TTT ttt;

    public AsyncPlayerChatListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        final String CHAT_FORMAT = player.getDisplayName() + "§7: §f" + message;

        Optional<GamePlayer> optionalGamePlayer = ttt.getGameManager().getGamePlayer(player.getUniqueId());
        if (!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
        if (gamePlayer.isSpectator()) {
            event.setCancelled(true);
            ttt.getGameManager().getSpectators().forEach(gamePlayers -> gamePlayers.getPlayer().sendMessage(CHAT_FORMAT));
            return;
        }

        event.setFormat(CHAT_FORMAT);
    }
}
