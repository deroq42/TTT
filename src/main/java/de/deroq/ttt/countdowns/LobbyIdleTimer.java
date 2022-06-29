package de.deroq.ttt.countdowns;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;

public class LobbyIdleTimer extends TimerTask {

    public LobbyIdleTimer(TTT ttt) {
        super(ttt, false, 20*60*3, 20*60*3);
        setCurrentSeconds(0);
        setTotalSeconds(600);
    }

    @Override
    public void onTick() {
        if(Bukkit.getOnlinePlayers().size() < Constants.NEEDED_PLAYERS) {
            Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage(Constants.PREFIX + "Es werden §3" + Constants.NEEDED_PLAYERS + " Spieler §7benötigt, um die Runde zu starten"));
        }
    }

    @Override
    public void onFinish() {
        if(Bukkit.getOnlinePlayers().size() < Constants.NEEDED_PLAYERS) {
            Bukkit.shutdown();
        }
    }
}
