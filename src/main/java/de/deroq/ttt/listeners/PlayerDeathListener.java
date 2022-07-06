package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class PlayerDeathListener implements Listener {

    private final TTT ttt;

    public PlayerDeathListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        Optional<GamePlayer> optionalKilledGamePlayer = ttt.getGameManager().getGamePlayer(killed.getUniqueId());
        Optional<GamePlayer> optionalKillerGamePlayer = ttt.getGameManager().getGamePlayer(killer.getUniqueId());
        if(!optionalKilledGamePlayer.isPresent() || !optionalKillerGamePlayer.isPresent()) {
            return;
        }

        GamePlayer killedGamePlayer = optionalKilledGamePlayer.get();
        GamePlayer killerGamePlayer = optionalKillerGamePlayer.get();
        Role killedRole = killedGamePlayer.getRole();
        Role killerRole = killerGamePlayer.getRole();

        event.setDeathMessage(null);
        event.getDrops().clear();

        killed.sendMessage(Constants.PREFIX + "Du wurdest von " + killerRole.getColorCode() + killer.getName() + " §7getötet");
        killer.sendMessage(Constants.PREFIX + "Du hast §3" + killed.getName() + " §7getötet");
        killer.sendMessage(Constants.PREFIX + "Du hast einen " + killedRole.getColorCode() + killedRole.getName() + " §7getötet");

       ttt.getGameManager().setSpectator(killed);
        ttt.getGameManager().checkForWin();
    }
}
