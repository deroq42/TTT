package de.deroq.ttt.commands.misc;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SetLobbyCommand implements CommandExecutor {

    private final TTT ttt;

    public SetLobbyCommand(TTT ttt) {
        this.ttt = ttt;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if(!player.hasPermission("ttt.setup")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        ttt.getFileManager().getSettingsConfig().setWaitingLobbyLocation(BukkitUtils.locationToString(player.getLocation()));
        try {
            ttt.getFileManager().saveConfig(ttt.getFileManager().getSettingsConfig());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(Constants.PREFIX + "§aLobby wurde gesetzt");
        return false;
    }
}
