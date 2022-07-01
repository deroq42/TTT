package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final TTT ttt;

    public PlayerDeathListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        GamePlayer killedGamePlayer = ttt.getGameManager().getGamePlayer(killed.getUniqueId()).get();
        GamePlayer killerGamePlayer = ttt.getGameManager().getGamePlayer(killer.getUniqueId()).get();
        Role killedRole = killedGamePlayer.getRole();
        Role killerRole = killerGamePlayer.getRole();

        event.setDeathMessage(null);
        event.getDrops().clear();

        killed.sendMessage(Constants.PREFIX + "Du wurdest von " + killerRole.getColorCode() + killer.getName() + " §7getötet");
        killer.sendMessage(Constants.PREFIX + "Du hast §3" + killed.getName() + " §7getötet");
        killer.sendMessage(Constants.PREFIX + "Du hast einen " + killedRole.getColorCode() + killedRole.getText() + " §7getötet");

       ttt.getGameManager().setSpectator(killed);
        ttt.getGameManager().checkForWin();
    }
}
