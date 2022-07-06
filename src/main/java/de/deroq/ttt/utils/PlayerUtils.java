package de.deroq.ttt.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static void loadInventory(Player player, GameState gameState) {
        player.getInventory().setItem(8, Constants.LOBBY_ITEM);

        if (gameState != GameState.LOBBY) {
            player.getInventory().setItem(0, Constants.SPECTATOR_ITEM);
        }
    }

    public static void loadPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
    }
}
