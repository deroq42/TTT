package de.deroq.ttt.timers.lobby;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.map.models.GameMap;
import de.deroq.ttt.timers.ingame.ProtectionTimer;
import de.deroq.ttt.timers.TimerTask;
import de.deroq.ttt.utils.BukkitUtils;
import de.deroq.ttt.utils.Constants;
import de.deroq.ttt.utils.GameState;
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
        if(ttt.getGameManager().getGameState() != GameState.LOBBY || (Bukkit.getOnlinePlayers().size() < ttt.getGameManager().MIN_PLAYERS && !ttt.getGameManager().isForceStarted())) {
            onStop();
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setLevel(currentSeconds);
            player.setExp((float) currentSeconds / TOTAL_SECONDS);
        });


        if(Arrays.asList(60, 30, 10, 5, 4, 3, 2, 1).contains(currentSeconds)) {
            BukkitUtils.sendBroadcastMessage("Die Runde startet in §3" + currentSeconds + " " + (currentSeconds != 1 ? "Sekunden" : "Sekunde"));
            BukkitUtils.sendBroadcastSound(Sound.BLOCK_NOTE_BLOCK_BASS);

            if(currentSeconds == 10) {
                GameMap gameMap = ttt.getGameManager().getCurrentGameMap();
                BukkitUtils.sendBroadcastMessage("Es wird auf der Map §3" + gameMap.getMuid() + " §7gespielt, gebaut von: §3" + gameMap.getBuilders());
            }
        }
    }

    @Override
    public void onFinish() {
        ProtectionTimer protectionTimer = new ProtectionTimer(ttt);
        protectionTimer.onStart();
        onStop();

        ttt.getGameManager().setGameState(GameState.PROTECTION);
        ttt.getGameManager().teleportToSpawns();
        ttt.getGameManager().setCurrentTimer(protectionTimer);
    }
}
