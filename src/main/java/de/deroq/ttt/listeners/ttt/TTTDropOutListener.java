package de.deroq.ttt.listeners.ttt;

import de.deroq.ttt.TTT;
import de.deroq.ttt.events.TTTDropOutEvent;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.models.Role;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author deroq
 * @since 15.07.2022
 */

public class TTTDropOutListener implements Listener {

    private final TTT ttt;

    public TTTDropOutListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onTTTDropOut(TTTDropOutEvent event) {
        GamePlayer gamePlayer = event.getGamePlayer();

        ttt.getGameManager().setSpectator(gamePlayer, true);

        Role role = ttt.getGameManager().checkForWin();
        if(role != null) {
            ttt.getGameManager().onWin(role);
        }
    }
}
