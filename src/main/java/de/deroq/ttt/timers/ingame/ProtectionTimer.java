package de.deroq.ttt.timers.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
import org.bukkit.Sound;

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
        if (ttt.getGameManager().getGameState() != GameState.PROTECTION) {
            onStop();
        }

        if (Arrays.asList(30, 20, 10, 5, 4, 3, 2, 1).contains(currentSeconds)) {
            BukkitUtils.sendBroadcastMessage("Die Schutzzeit endet in §3" + currentSeconds + " " + (currentSeconds != 1 ? "Sekunden" : "Sekunde"));
            BukkitUtils.sendBroadcastSound(Sound.BLOCK_NOTE_BLOCK_BASS);
        }
    }

    @Override
    public void onFinish() {
        IngameTimer ingameTimer = new IngameTimer(ttt);
        ingameTimer.onStart();
        onStop();

        ttt.getGameManager().setGameState(GameState.INGAME);
        ttt.getGameManager().setCurrentTimer(ingameTimer);
        ttt.getGameManager().allocateRoles();

        BukkitUtils.sendBroadcastMessage("Die Schutzzeit ist vorbei");
        ttt.getGameManager().getAlive().forEach(gamePlayer -> {
            ttt.getGameManager().setIngameScoreboard(gamePlayer);
            gamePlayer.getPlayer().sendMessage(Constants.PREFIX + "Deine Rolle: " + gamePlayer.getRole().getColorCode() + gamePlayer.getRole().getName());
        });
    }
}
