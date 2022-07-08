package de.deroq.ttt.timers.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.timers.restart.RestartTimer;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.GameState;
import de.deroq.ttt.utils.PlayerUtils;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class IngameTimer extends TimerTask {

    //WHERE THE TIMER STOPS TO COUNT UP
    private final int TOTAL_SECONDS = 30*60;

    public IngameTimer(TTT ttt) {
        super(ttt, false, 0, 20);
        setTotalSeconds(TOTAL_SECONDS);
        setCurrentSeconds(0);
    }

    @Override
    public void onTick() {
        if(Arrays.asList(20*60, 25*60, 29*60).contains(currentSeconds)) {
            final int REMAINING_TIME = (TOTAL_SECONDS - currentSeconds) / 60;
            BukkitUtils.sendBroadcastMessage("Die Runde endet in §3" + REMAINING_TIME + " Minuten");
        }
    }

    @Override
    public void onFinish() {
        RestartTimer restartTimer = new RestartTimer(ttt);
        restartTimer.onStart();
        onStop();

        ttt.getGameManager().setGameState(GameState.RESTART);
        ttt.getGameManager().setCurrentTimer(restartTimer);

        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerUtils.loadPlayer(player);
            ttt.getGameManager().teleportToLobby(player);
        });
    }
}
