package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class EntityDamageListener implements Listener {

    private final TTT ttt;

    public EntityDamageListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(ttt.getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Optional<GamePlayer> optionalGamePlayer = ttt.getGameManager().getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
        if(gamePlayer.isSpectator()) {
            event.setCancelled(true);
        }
    }
}
