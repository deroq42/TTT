package de.deroq.ttt.managers;

import de.deroq.ttt.countdowns.Countdown;
import de.deroq.ttt.utils.GameState;

public class GameManager {

    private GameState gameState;
    private Countdown currentCountdown;
    private boolean forceStarted;

    public GameManager() {
        this.gameState = GameState.LOBBY;
        this.forceStarted = false;
    }



    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Countdown getCurrentCountdown() {
        return currentCountdown;
    }

    public void setCurrentCountdown(Countdown currentCountdown) {
        this.currentCountdown = currentCountdown;
    }

    public boolean isForceStarted() {
        return forceStarted;
    }

    public void setForceStarted(boolean forceStarted) {
        this.forceStarted = forceStarted;
    }
}

