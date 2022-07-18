package de.deroq.ttt.commands.map;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteMapCommand implements CommandExecutor {

    private final TTT ttt;

    public DeleteMapCommand(TTT ttt) {
        this.ttt = ttt;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("ttt.setup")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Constants.PREFIX + "§3/deleteMap <map>");
            return true;
        }

        String map = args[0];
        ttt.getGameMapManager().deleteMap(map).thenAcceptAsync(doesntExist -> {
            if(doesntExist) {
                player.sendMessage(Constants.PREFIX + "Diese Map gibt es nicht");
                return;
            }

            player.sendMessage(Constants.PREFIX + "§aMap wurde gelöscht");
        });

        return false;
    }
}
