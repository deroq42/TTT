package de.deroq.ttt.game.models;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ArrayList<String> lore = new ArrayList();
    private final ItemMeta meta;

    public ItemBuilder(Material mat, short subid, int amount) {
        this.item = new ItemStack(mat, amount, subid);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat, short subid) {
        this.item = new ItemStack(mat, 1, subid);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat, int amount) {
        this.item = new ItemStack(mat, amount, (short) 0);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material mat) {
        this.item = new ItemStack(mat, 1, (short) 0);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setAmount(int value) {
        item.setAmount(value);
        return this;
    }

    public ItemBuilder setEmptyDisplayName() {
        meta.setDisplayName(" ");
        return this;
    }

    public ItemBuilder setGlow() {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setUnglow() {
        if(meta == null) {
            return this;
        }

        if (meta.hasEnchant(Enchantment.DURABILITY)) {
            meta.removeEnchant(Enchantment.DURABILITY);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setData(short data) {
        item.setDurability(data);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        lore.add(line);
        return this;
    }

    public ItemBuilder addLoreArray(String[] lines) {
        lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder addLoreAll(List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        if(meta == null) {
            return this;
        }

        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        ((SkullMeta) meta).setOwner(owner);
        return this;
    }

    public ItemBuilder setColor(Color c) {
        ((LeatherArmorMeta) meta).setColor(c);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor c) {
        ((BannerMeta) meta).setBaseColor(c);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment ench, int lvl) {
        meta.addEnchant(ench, lvl, true);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder addLeatherColor(Color color) {
        ((LeatherArmorMeta) meta).setColor(color);
        return this;
    }

    public ItemStack build() {
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }
}