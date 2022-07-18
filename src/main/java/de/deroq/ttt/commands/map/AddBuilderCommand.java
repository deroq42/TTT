package de.deroq.ttt.commands.map;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddBuilderCommand implements CommandExecutor {

    private final TTT ttt;

    public AddBuilderCommand(TTT ttt) {
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

        if(args.length != 2) {
            player.sendMessage(Constants.PREFIX + "§3/addBuilder <map> <builder>");
            return true;
        }

        String map = args[0];
        ttt.getGameMapManager().getMap(map).thenAcceptAsync(gameMap -> {
            if(gameMap == null) {
                player.sendMessage(Constants.PREFIX + "Diese Map gibt es nicht");
                return;
            }

            gameMap.getBuilders().add(args[1]);
            ttt.getGameMapManager().updateMap(gameMap).thenAcceptAsync(b -> player.sendMessage(Constants.PREFIX + "§aBuilder wurde hinzugefügt"));
        });
        return false;
    }
}