package de.deroq.ttt.timers;

import de.deroq.ttt.TTT;
import de.deroq.ttt.utils.Constants;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Arrays;

public class LobbyTimer extends TimerTask {

    //WHERE THE TIMER BEGINS
    private final int TOTAL_SECONDS = 61;

    public LobbyTimer(TTT ttt) {
        super(ttt, true, 0, 20);
        setTotalSeconds(TOTAL_SECONDS);
        setCurrentSeconds(TOTAL_SECONDS);
    }

    @Override
    public void onTick() {
        if(Bukkit.getOnlinePlayers().size() < Constants.NEEDED_PLAYERS && !ttt.getGameManager().isForceStarted()) {
            onStop();
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setLevel(currentSeconds);
            player.setExp((float) currentSeconds / TOTAL_SECONDS);
        });


        if(Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1).contains(currentSeconds)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(Constants.PREFIX + "Die Runde startet in §3" + currentSeconds + " " + (currentSeconds != 1 ? "Sekunden" : "Sekunde"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 3f, 3f);
            });
        }
    }

    @Override
    public void onFinish() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(Constants.PREFIX + "Die Runde hat begonnen!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3f, 3f);
        });
    }
}
