package de.deroq.ttt.listeners;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final TTT ttt;

    public PlayerInteractListener(TTT ttt) {
        this.ttt = ttt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Optional<GamePlayer> optionalGamePlayer = ttt.getGameManager().getGamePlayer(player.getUniqueId());
        if (!optionalGamePlayer.isPresent()) {
            return;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
        if (gamePlayer.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block == null) {
                return;
            }

            if (block.getType() == Material.CHEST) {
                if (ttt.getGameManager().getGameState() != GameState.PROTECTION || ttt.getGameManager().getGameState() != GameState.INGAME) {
                    return;
                }

                event.setCancelled(true);
                ttt.getGameManager().lootRandomWeapon(gamePlayer, block);
                return;
            }

            if (block.getType() == Material.ENDER_CHEST) {
                if (ttt.getGameManager().getGameState() != GameState.INGAME) {
                    return;
                }

                event.setCancelled(true);
                ttt.getGameManager().lootIronSword(gamePlayer, block);
                return;
            }

            if (block.getType().toString().endsWith("BUTTON")) {
                if (ttt.getGameManager().getGameState() != GameState.INGAME) {
                    return;
                }

                if (block.getType() == Material.OAK_BUTTON) {
                    ttt.getGameManager().triggerTraitorTrap(gamePlayer);
                    return;
                }

                if (block.getType() == Material.STONE_BUTTON) {
                    ttt.getGameManager().enterTraitorTester(gamePlayer);
                    return;
                }
                return;
            }

            if (ttt.getGameManager().getGameState() == GameState.LOBBY || ttt.getGameManager().getGameState() == GameState.RESTART) {
                if (block instanceof InventoryHolder) {
                    event.setCancelled(true);
                }
                return;
            }
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemStack = event.getItem();

            if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
                return;
            }

            if (itemStack.isSimilar(Constants.LOBBY_ITEM)) {
                player.kickPlayer("");
                return;
            }

            ttt.getGameShopManager().handleInteract(gamePlayer, itemStack);
        }
    }
}
