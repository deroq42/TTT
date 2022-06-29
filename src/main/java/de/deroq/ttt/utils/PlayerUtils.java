package de.deroq.ttt.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static void loadInventory(Player player, GameState gameState) {
        player.getInventory().setItem(8, Constants.LOBBY_ITEM);

        if (gameState == GameState.LOBBY) {
            player.getInventory().setItem(4, Constants.ROLE_PASS_ITEM);
            return;
        }

        if (gameState == GameState.RESTART) {
            player.getInventory().setItem(0, Constants.SPECTATOR_ITEM);
        }
    }

    public static void loadPlayer(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.setOp(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
    }
}
