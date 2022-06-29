package de.deroq.ttt.utils;

import de.deroq.ttt.models.misc.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Constants {

    public static final String PREFIX = "§7[§3TTT§7] ";
    public static final String LOBBY_LOCATION_NAME = "lobby";

    public static final int NEEDED_PLAYERS = 2;

    public static final ItemStack ROLE_PASS_ITEM = new ItemBuilder(Material.TORCH).setDisplayName("§cPässe").build();
    public static final ItemStack LOBBY_ITEM = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§aZur Lobby").build();
    public static final ItemStack SPECTATOR_ITEM = new ItemBuilder(Material.CLOCK).setDisplayName("§eSpieler zuschauen").build();
    public static final ItemStack DETECTIVE_PASS_ITEM = new ItemBuilder(Material.BLUE_WOOL).setDisplayName("§9Detective-Pass").build();
    public static final ItemStack TRAITOR_PASS_ITEM = new ItemBuilder(Material.RED_WOOL).setDisplayName("§4Traitor-Pass").build();
}
