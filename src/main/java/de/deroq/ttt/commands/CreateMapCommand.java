package de.deroq.ttt.commands;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateMapCommand extends Command {

    private final TTT ttt;

    public CreateMapCommand(String name, TTT ttt) {
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

        String id = args[0];
        ttt.getGameMapManager().createMap(id).thenAcceptAsync(exists -> {
            if (exists) {
                player.sendMessage(Constants.PREFIX + "§cDiese Map gibt es bereits");
                return;
            }

            player.sendMessage(Constants.PREFIX + "§aMap wurde erstellt");
        });

        return false;
    }
}
