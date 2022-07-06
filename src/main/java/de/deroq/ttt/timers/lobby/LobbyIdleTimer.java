package de.deroq.ttt.timers.lobby;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Bukkit;

public class LobbyIdleTimer extends TimerTask {

    //WHERE THE TIMER STOPS TO COUNT UP
    private final int TOTAL_SECONDS = 600;
    private final int MIN_PLAYERS;

    public LobbyIdleTimer(TTT ttt) {
        super(ttt, false, 20*60*3, 20*60*3);
        this.MIN_PLAYERS = ttt.getFileManager().getSettingsConfig().getMinPlayers();

        setCurrentSeconds(0);
        setTotalSeconds(TOTAL_SECONDS);
    }

    @Override
    public void onTick() {
        if(ttt.getGameManager().getGameState() != GameState.LOBBY) {
            onStop();
            return;
        }

        if(Bukkit.getOnlinePlayers().size() < MIN_PLAYERS) {
            BukkitUtils.sendBroadcastMessage("Es werden §3" + MIN_PLAYERS + " Spieler §7benötigt, um die Runde zu starten");
        }
    }

    @Override
    public void onFinish() {
        if(Bukkit.getOnlinePlayers().size() < MIN_PLAYERS) {
            Bukkit.shutdown();
        }
    }
}
