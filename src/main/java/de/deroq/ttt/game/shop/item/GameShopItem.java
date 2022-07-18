package de.deroq.ttt.game.shop.item;

import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.models.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author deroq
 * @since 18.07.2022
 */

public abstract class GameShopItem {

    protected final String name;
    protected final Material material;
    protected final int amount;
    protected final List<String> description;
    protected final Map<Enchantment, Integer> enchantments;
    protected int durability;
    protected final int price;

    public GameShopItem(String name, Material material, int amount, List<String> description, Map<Enchantment, Integer> enchantments, int durability, int price) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.description = description;
        this.enchantments = enchantments;
        this.durability = durability;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getDescription() {
        return description;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack asItemStack() {
        return new ItemBuilder(material)
                .setDisplayName(name)
                .setAmount(amount)
                .setDurability(durability)
                .addLoreLine("§fPreis: §d" + price + " Punkte")
                .addLoreAll(description)
                .addEnchantmentAll(enchantments)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public abstract void onInteract(GamePlayer gamePlayer);
}
