package de.deroq.ttt.timers.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.timers.TimerTask;

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

    }

    @Override
    public void onFinish() {

    }
}
