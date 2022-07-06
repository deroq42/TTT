package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.models.Role;
import de.deroq.ttt.utils.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class EntityDamageByEntityListener implements Listener {

    private final TTT ttt;

    public EntityDamageByEntityListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(ttt.getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        Optional<GamePlayer> optionalDamagedGamePlayer = ttt.getGameManager().getGamePlayer(damaged.getUniqueId());
        Optional<GamePlayer> optionalDamagerGamePlayer = ttt.getGameManager().getGamePlayer(damager.getUniqueId());
        if(!optionalDamagedGamePlayer.isPresent() || !optionalDamagerGamePlayer.isPresent()) {
            return;
        }

        GamePlayer damagedGamePlayer = optionalDamagedGamePlayer.get();
        GamePlayer damagerGamePlayer = optionalDamagerGamePlayer.get();

        if(damagedGamePlayer.isSpectator() || damagerGamePlayer.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        if(damagedGamePlayer.getRole() == damagerGamePlayer.getRole() && damagedGamePlayer.getRole() != Role.INNOCENT) {
            event.setDamage(0);
        }
    }
}
