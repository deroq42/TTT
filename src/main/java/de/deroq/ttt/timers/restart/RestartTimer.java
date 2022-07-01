package de.deroq.ttt.timers.restart;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GameMap;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Arrays;

public class RestartTimer extends TimerTask {

    //WHERE THE TIMER BEGINS
    private final int TOTAL_SECONDS = 21;

    public RestartTimer(TTT ttt) {
        super(ttt, true, 0, 20);
        setTotalSeconds(TOTAL_SECONDS);
        setCurrentSeconds(TOTAL_SECONDS);
    }

    @Override
    public void onTick() {
        if(ttt.getGameManager().getGameState() != GameState.RESTART) {
            onStop();
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setLevel(currentSeconds);
            player.setExp((float) currentSeconds / TOTAL_SECONDS);
        });


        if(Arrays.asList(20, 10, 5, 4, 3, 2, 1).contains(currentSeconds)) {
            BukkitUtils.sendBroadcastMessage("Die Runde startet in §3" + currentSeconds + " " + (currentSeconds != 1 ? "Sekunden" : "Sekunde") + " §7neu");
        }
    }

    @Override
    public void onFinish() {
        Bukkit.shutdown();
    }
}
