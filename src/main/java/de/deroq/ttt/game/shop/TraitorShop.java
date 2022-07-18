package de.deroq.ttt.game.shop;

import de.deroq.ttt.game.models.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class TraitorShop implements InventoryHolder {

    private final Inventory traitorShopInventory;

    public TraitorShop() {
        this.traitorShopInventory = initInventory();
    }

    private Inventory initInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "§8Traitor-Shop");

        inventory.setItem(0, new ItemBuilder(Material.GREEN_STAINED_GLASS)
                .setDisplayName("§aInnocent-Ticket")
                .build());

        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return traitorShopInventory;
    }
}
