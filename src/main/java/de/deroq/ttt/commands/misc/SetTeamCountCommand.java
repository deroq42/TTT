package de.deroq.ttt.commands.misc;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import jnr.ffi.annotations.In;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SetTeamCountCommand extends Command {

    private final TTT ttt;

    public SetTeamCountCommand(String name, TTT ttt) {
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

        int i = 0;
        try {
            i = Integer.parseInt(args[0]);
        } catch(NumberFormatException exception) {
            player.sendMessage(Constants.PREFIX + "§cGib eine valide Zahl an!");
            return true;
        }

        ttt.getFileManager().getSettingsConfig().setMinPlayers(i);
        try {
            ttt.getFileManager().saveConfig(ttt.getFileManager().getSettingsConfig());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendMessage(Constants.PREFIX + "§aDie Mindestanzahl an Spielern wurde auf " + i + " gesetzt");
        return false;
    }
}
