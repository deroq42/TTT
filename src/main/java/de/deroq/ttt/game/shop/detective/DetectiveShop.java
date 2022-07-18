package de.deroq.ttt.game.shop.detective;

import de.deroq.ttt.game.shop.detective.items.OneShotBow;
import de.deroq.ttt.game.shop.item.GameShopItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class DetectiveShop implements InventoryHolder {

    private final Map<ItemStack, GameShopItem> detectiveShopItems;
    private final Inventory detectiveShopInventory;

    public DetectiveShop() {
        this.detectiveShopItems = loadDetectiveShopItems();
        this.detectiveShopInventory = initDetectiveShopInventory();
    }

    private Map<ItemStack, GameShopItem> loadDetectiveShopItems() {
        Map<ItemStack, GameShopItem> detectiveShopItems = new HashMap<>();
        OneShotBow oneShotBow = new OneShotBow();
        detectiveShopItems.put(oneShotBow.asItemStack(), oneShotBow);
        return detectiveShopItems;
    }

    private Inventory initDetectiveShopInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, "§8Detective-Shop");
        detectiveShopItems.values()
                .stream()
                .map(GameShopItem::asItemStack)
                .forEach(inventory::addItem);
        return inventory;
    }

    public Optional<GameShopItem> getShopItemByItemStack(ItemStack itemStack) {
        return detectiveShopItems.values()
                .stream()
                .filter(gameShopItem -> gameShopItem.asItemStack().isSimilar(itemStack))
                .findFirst();
    }

    @Override
    public Inventory getInventory() {
        return detectiveShopInventory;
    }
}
