package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.events.TTTDropOutEvent;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;
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
        event.setDeathMessage(null);
        event.getDrops().clear();

        Optional<GamePlayer> optionalKilledGamePlayer = ttt.getGameManager().getGamePlayer(killed.getUniqueId());
        if(!optionalKilledGamePlayer.isPresent()) {
            return;
        }

        GamePlayer killedGamePlayer = optionalKilledGamePlayer.get();
        Role killedRole = killedGamePlayer.getRole();
        Player killer = Bukkit.getPlayer(killedGamePlayer.getLastDamager());
        GamePlayer killerGamePlayer;

        if(killer == null) {
            killed.sendMessage(Constants.PREFIX + "Du bist gestorben");
        } else {
            Optional<GamePlayer> optionalKillerGamePlayer = ttt.getGameManager().getGamePlayer(killer.getUniqueId());
            if(!optionalKillerGamePlayer.isPresent()) {
                return;
            }

            killerGamePlayer = optionalKillerGamePlayer.get();
            Role killerRole = killerGamePlayer.getRole();

            killed.sendMessage(Constants.PREFIX + "Du wurdest von " + killerRole.getColorCode() + killer.getName() + " §7getötet");
            killer.sendMessage(Constants.PREFIX + "Du hast §3" + killed.getName() + " §7getötet");
            killer.sendMessage(Constants.PREFIX + "Du hast einen " + killedRole.getColorCode() + killedRole.getName() + " §7getötet");

            if(killerRole != killedRole && killerRole != Role.INNOCENT) {
                killerGamePlayer.addShopPoints(killedRole.getShopPoints());
            }
        }

        Bukkit.getPluginManager().callEvent(new TTTDropOutEvent(killedGamePlayer));
    }
}
