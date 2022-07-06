package de.deroq.ttt.commands.game;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceMapCommand extends Command {

    private final TTT ttt;

    public ForceMapCommand(String name, TTT ttt) {
        super(name);
        this.ttt = ttt;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("ttt.forcemap")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        if(args.length != 1) {
            player.sendMessage(Constants.PREFIX + "§3/forcemap <map>");
            return true;
        }

        if (ttt.getGameManager().getGameState() != GameState.LOBBY) {
            player.sendMessage(Constants.PREFIX + "Die Runde hat bereits begonnen");
            return true;
        }

        String map = args[0];
        if(!ttt.getGameMapManager().getMapCache().containsKey(map)) {
            player.sendMessage(Constants.PREFIX + "§3Verfügbare Maps: " + ttt.getGameMapManager().getMapCache().keySet());
            return true;
        }

        ttt.getGameManager().setCurrentGameMap(ttt.getGameMapManager().getMapCache().get(map));
        player.sendMessage(Constants.PREFIX + "§aMap wurde geändert");
        return false;
    }
}
