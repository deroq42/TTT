package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final TTT ttt;

    public PlayerInteractListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(itemStack.isSimilar(Constants.LOBBY_ITEM)) {
                player.kickPlayer("");
                return;
            }

            if(itemStack.isSimilar(Constants.ROLE_PASS_ITEM)) {
                player.openInventory(ttt.getGameManager().getRolePassInventory());
                return;
            }
        }
    }
}
