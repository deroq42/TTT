package de.deroq.ttt.game.models;

import org.bukkit.Material;
import org.bukkit.Sound;

public enum Role {

    INNOCENT("§a", "Innocent", Material.GREEN_STAINED_GLASS, Sound.BLOCK_NOTE_BLOCK_PLING, 1),
    DETECTIVE("§9", "Detective", null, null, 3),
    TRAITOR("§4", "Traitor", Material.RED_STAINED_GLASS, Sound.BLOCK_NOTE_BLOCK_BASS, 1);

    private final String colorCode;
    private final String name;
    private final Material testerLight;
    private final Sound testerSound;
    private final int shopPoints;

    Role(String colorCode, String name, Material testerLight, Sound testerSound, int shopPoints) {
        this.colorCode = colorCode;
        this.name = name;
        this.testerLight = testerLight;
        this.testerSound = testerSound;
        this.shopPoints = shopPoints;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getName() {
        return name;
    }

    public Material getTesterLight() {
        return testerLight;
    }

    public Sound getTesterSound() {
        return testerSound;
    }

    public int getShopPoints() {
        return shopPoints;
    }
}
