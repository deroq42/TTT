package de.deroq.ttt.commands;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.lobby.LobbyTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends Command {

    private final TTT ttt;

    public StartCommand(String name, TTT ttt) {
        super(name);
        this.ttt = ttt;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if(!player.hasPermission("ttt.start")) {
            player.sendMessage(Constants.PREFIX + "§cDazu hast du keine Rechte");
            return true;
        }

        if(ttt.getGameManager().getGameState() != GameState.LOBBY) {
            player.sendMessage(Constants.PREFIX + "Die Runde hat bereits begonnen");
            return true;
        }

        TimerTask currentTimer = ttt.getGameManager().getCurrentTimer();
        if(currentTimer instanceof LobbyTimer) {
            if(currentTimer.getCurrentSeconds() <= 10) {
                player.sendMessage(Constants.PREFIX + "Die Runde startet bereits");
                return true;
            }
        } else {
            currentTimer = ttt.getGameManager().createLobbyTimer();
        }

        currentTimer.setCurrentSeconds(11);
        ttt.getGameManager().setForceStarted(true);
        player.sendMessage(Constants.PREFIX + "§aDie Runde wird gestartet");
        return false;
    }
}
