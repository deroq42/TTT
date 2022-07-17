package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.utils.GameState;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;

public class EntityDamageByEntityListener implements Listener {

    private final TTT ttt;

    public EntityDamageByEntityListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (ttt.getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = null;

        Optional<GamePlayer> optionalDamagedGamePlayer = ttt.getGameManager().getGamePlayer(damaged.getUniqueId());
        if (!optionalDamagedGamePlayer.isPresent()) {
            return;
        }

        GamePlayer damagedGamePlayer = optionalDamagedGamePlayer.get();
        if (damagedGamePlayer.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK) {
            if (!(event.getDamager() instanceof Player)) {
                return;
            }

            damager = (Player) event.getDamager();

        } else if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (!(event.getDamager() instanceof Arrow)) {
                return;
            }

            Arrow arrow = (Arrow) event.getDamager();
            damager = (Player) arrow.getShooter();
        }

        if(damager == null) {
            return;
        }

        Optional<GamePlayer> optionalDamagerGamePlayer = ttt.getGameManager().getGamePlayer(damager.getUniqueId());
        if (!optionalDamagerGamePlayer.isPresent()) {
            return;
        }

        GamePlayer damagerGamePlayer = optionalDamagerGamePlayer.get();
        if (damagerGamePlayer.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        if (damagedGamePlayer.getRole() == damagerGamePlayer.getRole() && damagedGamePlayer.getRole() != Role.INNOCENT) {
            event.setDamage(0);
        }
    }
}
