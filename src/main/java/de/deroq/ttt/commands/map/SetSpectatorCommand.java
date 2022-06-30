package de.deroq.ttt.commands.map;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpectatorCommand extends Command {

    private final TTT ttt;

    public SetSpectatorCommand(String name, TTT ttt) {
        super(name);
        this.ttt = ttt;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("ttt.setup")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(Constants.PREFIX + "§3/setSpectator <map>");
            return true;
        }

        String map = args[0];
        ttt.getGameMapManager().getMap(map).thenAcceptAsync(gameMap -> {
            if (gameMap == null) {
                player.sendMessage(Constants.PREFIX + "Diese Map gibt es nicht");
                return;
            }

            gameMap.setSpectatorLocation(BukkitUtils.locationToString(player.getLocation()));
            ttt.getGameMapManager().updateMap(gameMap).thenAcceptAsync(b -> player.sendMessage(Constants.PREFIX + "§aSpectatorlocation wurde gesetzt"));
        });
        return false;
    }
}
