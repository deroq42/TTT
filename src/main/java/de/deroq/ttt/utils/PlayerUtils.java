package de.deroq.ttt.utils;

import org.bukkit.entity.Player;

public class PlayerUtils {

    public static void loadInventory(Player player, GameState gameState) {
        if(gameState == GameState.LOBBY) {
            player.getInventory().setItem(0, );
        }
    }
}
