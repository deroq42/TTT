package de.deroq.ttt.game.shop.traitor;

import de.deroq.ttt.game.shop.item.GameShopItem;
import de.deroq.ttt.game.shop.traitor.items.InnocentTicket;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class TraitorShop implements InventoryHolder {

    private final Map<ItemStack, GameShopItem> traitorShopItems;
    private final Inventory traitorShopInventory;

    public TraitorShop() {
        this.traitorShopItems = loadTraitorShopItems();
        this.traitorShopInventory = initTraitorShopInventory();
    }

    private Map<ItemStack, GameShopItem> loadTraitorShopItems() {
        Map<ItemStack, GameShopItem> traitorShopItems = new HashMap<>();
        InnocentTicket innocentTicket = new InnocentTicket();
        traitorShopItems.put(innocentTicket.asItemStack(), innocentTicket);
        return traitorShopItems;
    }

    private Inventory initTraitorShopInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, "§8Traitor-Shop");
        traitorShopItems.values()
                .stream()
                .map(GameShopItem::asItemStack)
                .forEach(inventory::addItem);
        return inventory;
    }

    public Optional<GameShopItem> getShopItemByItemStack(ItemStack itemStack) {
        return traitorShopItems.values()
                .stream()
                .filter(gameShopItem -> gameShopItem.asItemStack().isSimilar(itemStack))
                .findFirst();
    }

    @Override
    public Inventory getInventory() {
        return traitorShopInventory;
    }
}
