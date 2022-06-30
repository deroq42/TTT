package de.deroq.ttt.utils;

import de.deroq.ttt.models.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Constants {
    
    public static final String PREFIX = "§7[§3TTT§7] ";
    public static final String LOBBY_LOCATION_NAME = "lobby";

    public static final int NEEDED_PLAYERS = 1;
    public static final double DETECTIVE_ALLOCATE_RATE = 0.15;
    public static final double TRAITOR_ALLOCATE_RATE = 0.34;

    public static final ItemStack LOBBY_ITEM = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§aZur Lobby").build();
    public static final ItemStack SPECTATOR_ITEM = new ItemBuilder(Material.CLOCK).setDisplayName("§eSpieler zuschauen").build();
}
