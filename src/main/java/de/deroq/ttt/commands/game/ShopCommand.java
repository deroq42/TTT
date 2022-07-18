package de.deroq.ttt.commands.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.game.shop.TraitorShop;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author deroq
 * @since 18.07.2022
 */

public class ShopCommand implements CommandExecutor  {

    private final TTT ttt;

    public ShopCommand(TTT ttt) {
        this.ttt = ttt;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        if(ttt.getGameManager().getGameState() != GameState.INGAME) {
            player.sendMessage(Constants.PREFIX + "Das Spiel hat noch nicht begonnen");
            return true;
        }

        Optional<GamePlayer> optionalGamePlayer = ttt.getGameManager().getGamePlayer(player.getUniqueId());
        if(!optionalGamePlayer.isPresent()) {
            return true;
        }

        GamePlayer gamePlayer = optionalGamePlayer.get();
        if(gamePlayer.isSpectator()) {
            player.sendMessage(Constants.PREFIX + "Du lebst nicht mehr");
            return true;
        }

        Role role = gamePlayer.getRole();
        if(role == Role.INNOCENT) {
            player.sendMessage(Constants.PREFIX + "Innocents haben keinen Shop");
            return true;
        }

        if(role == Role.TRAITOR) {
            TraitorShop traitorShop = new TraitorShop();
            player.openInventory(traitorShop.getInventory());
        }

       return false;
    }
}
