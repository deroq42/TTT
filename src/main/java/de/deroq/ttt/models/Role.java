package de.deroq.ttt.models;

import org.bukkit.Material;
import org.bukkit.Sound;

public enum Role {

    INNOCENT("§a", "Innocent", Material.GREEN_STAINED_GLASS, Sound.BLOCK_NOTE_BLOCK_PLING),
    DETECTIVE("§9", "Detective", null, null),
    TRAITOR("§4", "Traitor", Material.RED_STAINED_GLASS, Sound.BLOCK_NOTE_BLOCK_BASS);

    private final String colorCode;
    private final String text;
    private final Material testerLight;
    private final Sound testerSound;

    Role(String colorCode, String text, Material testerLight, Sound testerSound) {
        this.colorCode = colorCode;
        this.text = text;
        this.testerLight = testerLight;
        this.testerSound = testerSound;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getText() {
        return text;
    }

    public Material getTesterLight() {
        return testerLight;
    }

    public Sound getTesterSound() {
        return testerSound;
    }
}
