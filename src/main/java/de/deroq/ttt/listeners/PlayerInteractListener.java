package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Button;

public class PlayerInteractListener implements Listener {

    private final TTT ttt;

    public PlayerInteractListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if(block == null) {
                return;
            }

            if(block.getType().toString().endsWith("BUTTON")) {
                Button button = (Button) block.getState().getData();
                Block blockBehind = block.getRelative(button.getAttachedFace());

                if(blockBehind.getType() == Material.DIAMOND_BLOCK) {
                    ttt.getGameManager().triggerTraitorTrap();
                    return;
                }
            }
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();

            if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
                return;
            }

            if(itemStack.isSimilar(Constants.LOBBY_ITEM)) {
                player.kickPlayer("");
                return;
            }
        }
    }
}
