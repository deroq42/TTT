package de.deroq.ttt.commands;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SetLobbyCommand extends Command {

    private final TTT ttt;

    public SetLobbyCommand(String name, TTT ttt) {
        super(name);
        this.ttt = ttt;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if(!player.hasPermission("ttt.setup")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        ttt.getFileManager().getLocationsConfig().getLocations().put(Constants.LOBBY_LOCATION_NAME, BukkitUtils.locationToString(player.getLocation()));
        try {
            ttt.getFileManager().saveConfig(ttt.getFileManager().getLocationsConfig());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(Constants.PREFIX + "§aLobby wurde gesetzt");
        return false;
    }
}
