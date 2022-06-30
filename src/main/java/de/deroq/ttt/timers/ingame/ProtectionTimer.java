package de.deroq.ttt.timers.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.timers.ingame.IngameTimer;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ProtectionTimer extends TimerTask {

    //WHERE THE TIMER BEGINS
    private final int TOTAL_SECONDS = 31;

    public ProtectionTimer(TTT ttt) {
        super(ttt, true, 0, 20);
        setTotalSeconds(TOTAL_SECONDS);
        setCurrentSeconds(TOTAL_SECONDS);
    }

    @Override
    public void onTick() {
        if(ttt.getGameManager().getGameState() != GameState.PROTECTION) {
            onStop();
        }

        if(Arrays.asList(30, 20, 10, 5, 4, 3, 2, 1).contains(currentSeconds)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(Constants.PREFIX + "Die Schutzzeit endet in §3" + currentSeconds + " " + (currentSeconds != 1 ? "Sekunden" : "Sekunde"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 3f, 3f);
            });
        }
    }

    @Override
    public void onFinish() {
        IngameTimer ingameTimer = new IngameTimer(ttt);
        ingameTimer.onStart();
        onStop();

        ttt.getGameManager().setGameState(GameState.INGAME);
        ttt.getGameManager().setCurrentTimer(ingameTimer);
        //ROLLEN VERGEBEN

        ttt.getGameManager().getAlive().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            player.sendMessage(Constants.PREFIX + "Die Schutzzeit ist vorbei");
            player.sendMessage(Constants.PREFIX + "Deine Rolle: " + gamePlayer.getRole().getText());
        });
    }
}
