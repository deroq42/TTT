package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.shop.TraitorShop;
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

        if(ttt.getGameManager().getGameState() != GameState.INGAME) {
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

        if(event.getClickedInventory().getHolder() instanceof TraitorShop) {
            event.setCancelled(true);
            System.out.println("Jo");
        }
    }
}
