package de.deroq.ttt.game.shop;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.game.shop.detective.DetectiveShop;
import de.deroq.ttt.game.shop.item.GameShopItem;
import de.deroq.ttt.game.shop.traitor.TraitorShop;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class GameShopManager {

    private final TTT ttt;
    private final TraitorShop traitorShop;
    private final DetectiveShop detectiveShop;

    public GameShopManager(TTT ttt) {
        this.ttt = ttt;
        this.traitorShop = new TraitorShop();
        this.detectiveShop = new DetectiveShop();
    }

    public void handleInventoryClick(GamePlayer gamePlayer, ItemStack itemStack) {
        Player player = gamePlayer.getPlayer();
        Role role = gamePlayer.getRole();

        if (role == Role.INNOCENT) {
            return;
        }

        GameShopItem shopItem;
        Optional<GameShopItem> optionalShopItem;

        if(role == Role.TRAITOR) {
            optionalShopItem = traitorShop.getShopItemByItemStack(itemStack);
        } else {
            optionalShopItem = detectiveShop.getShopItemByItemStack(itemStack);
        }

        if (!optionalShopItem.isPresent()) {
            return;
        }

        shopItem = optionalShopItem.get();
        int price = shopItem.getPrice();

        if(gamePlayer.getShopPoints() < price) {
            player.sendMessage(Constants.PREFIX + "Du hast nicht genug Punkte");
            return;
        }

        player.getInventory().addItem(itemStack);
        gamePlayer.removeShopPoints(price);
        player.sendMessage(Constants.PREFIX + "Du hast dir das Item " + shopItem.getName() + " §7gekauft");
    }

    public void handleInteract(GamePlayer gamePlayer, ItemStack itemStack) {
        Material type = itemStack.getType();
        if(ttt.getGameManager().LOOTABLE_ITEMS.contains(type) || type == Material.IRON_SWORD || type == Material.ARROW) {
            return;
        }
        Role role = gamePlayer.getRole();

        if (role == Role.INNOCENT) {
            return;
        }

        GameShopItem shopItem;
        Optional<GameShopItem> optionalShopItem;

        if(role == Role.TRAITOR) {
            optionalShopItem = traitorShop.getShopItemByItemStack(itemStack);
        } else {
            optionalShopItem = detectiveShop.getShopItemByItemStack(itemStack);
        }

        if (!optionalShopItem.isPresent()) {
            return;
        }

        shopItem = optionalShopItem.get();
        if(itemStack.isSimilar(shopItem.asItemStack())) {
            shopItem.onInteract(gamePlayer);
        }
    }

    private TraitorShop initTraitorShop() {
        return new TraitorShop();
    }

    public TraitorShop getTraitorShop() {
        return traitorShop;
    }

    public DetectiveShop getDetectiveShop() {
        return detectiveShop;
    }
}
