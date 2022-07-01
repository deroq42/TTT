package de.deroq.ttt.game.models;

import de.deroq.ttt.models.Role;
import de.deroq.ttt.utils.GameState;
import de.deroq.ttt.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private boolean spectator;
    private Role role;

    private GamePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public static GamePlayer create(UUID uuid) {
        return new GamePlayer(uuid);
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator, Collection<GamePlayer> alive) {
        this.spectator = spectator;

        Player player = getPlayer();
        if(player == null) {
            return;
        }

        if(spectator) {
            player.setGameMode(GameMode.ADVENTURE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 3), false);
            alive.forEach(gamePlayer -> gamePlayer.getPlayer().hidePlayer(player));
            PlayerUtils.loadInventory(player, GameState.INGAME);
            setRole(null);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            alive.forEach(gamePlayer -> gamePlayer.getPlayer().showPlayer(player));
        }

        player.setAllowFlight(spectator);
        player.setFlying(spectator);
        player.setCollidable(!spectator);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
