package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.shop.detective.DetectiveShop;
import de.deroq.ttt.game.shop.traitor.TraitorShop;
import de.deroq.ttt.utils.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class InventoryClickListener implements Listener {

    private final TTT ttt;

    public InventoryClickListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();

        if(itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return;
        }

        if(ttt.getGameManager().getGameState() == GameState.LOBBY) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            return;
        }

        Optional<GamePlayer> optionalGamePlayer = ttt.getGameManager().getGamePlayer(player.getUniqueId());
        if (!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
        if (gamePlayer.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        if(event.getInventory().getHolder() instanceof TraitorShop ||event.getInventory().getHolder() instanceof DetectiveShop) {
            event.setCancelled(true);
            ttt.getGameShopManager().handleInventoryClick(gamePlayer, itemStack);
        }
    }
}
