package de.deroq.ttt.game.shop.detective.items;

import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.shop.item.GameShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Collections;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class OneShotBow extends GameShopItem {

    public OneShotBow() {
        super("§4Oneshot-Bogen",
                Material.BOW,
                1,
                Collections.singletonList("§7Tötet den Gegner mit nur einem Schuss"),
                Collections.singletonMap(Enchantment.ARROW_DAMAGE, 100),
                384,
                3);
    }

    @Override
    public void onInteract(GamePlayer gamePlayer) {

    }
}
