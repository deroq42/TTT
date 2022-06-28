package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.countdowns.LobbyCountdown;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;
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
        if(Bukkit.getOnlinePlayers().size() == Constants.NEEDED_PLAYERS) {
            LobbyCountdown lobbyCountdown = new LobbyCountdown(ttt);
            lobbyCountdown.onStart();
            ttt.getGameManager().setCurrentCountdown(lobbyCountdown);
        }
    }
}
