package de.deroq.ttt.game.shop.traitor.items;

import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.shop.item.GameShopItem;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class InnocentTicket extends GameShopItem {

    public InnocentTicket() {
        super("§aInnocent-Ticket",
                Material.GREEN_STAINED_GLASS,
                1,
                Collections.singletonList("§7Der Traitor-Tester identifiziert dich als Innocent"),
                null,
                0,
                1);
    }

    @Override
    public void onInteract(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();
        gamePlayer.setInnocentTicket(true);
        player.sendMessage(Constants.PREFIX + "Du hast ein Innocent Ticket eingelöst");
        player.getInventory().remove(material);
    }
}
