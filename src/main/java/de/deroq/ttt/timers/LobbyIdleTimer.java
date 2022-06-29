package de.deroq.ttt.timers;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;

public class LobbyIdleTimer extends TimerTask {

    //WHERE THE TIMER STOPS TO COUNT UP
    private final int TOTAL_SECONDS = 600;

    public LobbyIdleTimer(TTT ttt) {
        super(ttt, false, 20*60*3, 20*60*3);
        setCurrentSeconds(0);
        setTotalSeconds(TOTAL_SECONDS);
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
