package de.deroq.ttt.commands.map;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddTesterLightCommand extends Command {

    private final TTT ttt;

    public AddTesterLightCommand(String name, TTT ttt) {
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

        if (args.length != 2) {
            player.sendMessage(Constants.PREFIX + "§3/addTesterLight <map> <right | left>");
            return true;
        }

        String map = args[0];
        ttt.getGameMapManager().getMap(map).thenAcceptAsync(gameMap -> {
            if (gameMap == null) {
                player.sendMessage(Constants.PREFIX + "Diese Map gibt es nicht");
                return;
            }

            switch (args[1].toUpperCase()) {
                case "RIGHT":
                    gameMap.setRightTesterLightLocation(BukkitUtils.locationToString(player.getTargetBlock(null, 5).getLocation()));
                    ttt.getGameMapManager().updateMap(gameMap).thenAcceptAsync(b -> player.sendMessage(Constants.PREFIX + "§aRechtes Licht wurde gesetzt"));
                    break;

                case "LEFT":
                    gameMap.setLeftTesterLightLocation(BukkitUtils.locationToString(player.getTargetBlock(null, 5).getLocation()));
                    ttt.getGameMapManager().updateMap(gameMap).thenAcceptAsync(b -> player.sendMessage(Constants.PREFIX + "§aLinkes Licht wurde gesetzt"));
                    break;

                default:
                    player.sendMessage(Constants.PREFIX + "§3/addTesterLight <map> <right | left>");
                    break;
            }
        });

        return false;
    }
}
