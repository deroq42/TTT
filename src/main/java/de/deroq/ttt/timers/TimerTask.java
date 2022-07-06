package de.deroq.ttt.timers;

import de.deroq.ttt.TTT;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class TimerTask {

    protected final TTT ttt;
    protected final boolean countdown;
    protected final long delay;
    protected final long period;
    protected BukkitTask bukkitTask;
    protected int totalSeconds;
    protected int currentSeconds;

    public TimerTask(TTT ttt, boolean countdown, long delay, long period) {
        this.ttt = ttt;
        this.countdown = countdown;
        this.delay = delay;
        this.period = period;
    }

    public void onStart() {
        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(countdown) {
                    currentSeconds--;

                    if(currentSeconds >= 1) {
                        onTick();
                        return;
                    }

                    onFinish();
                    onStop();

                } else {
                    currentSeconds++;

                    if(currentSeconds < totalSeconds) {
                        onTick();
                        return;
                    }

                    onFinish();
                    onStop();
                }
            }
        }.runTaskTimer(ttt, delay, period);
    }

    /**
     * Will be executed every tick.
     */
    public abstract void onTick();

    /**
     * Will be executed when the timer has been finished.
     */
    public abstract void onFinish();

    /**
     * Stops the timer.
     */
    public void onStop() {
        if(bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        currentSeconds = (countdown ? totalSeconds : 0);
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public boolean isCountdown() {
        return countdown;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public int getCurrentSeconds() {
        return currentSeconds;
    }

    public void setCurrentSeconds(int currentSeconds) {
        this.currentSeconds = currentSeconds;
    }

    public BukkitTask getBukkitTask() {
        return bukkitTask;
    }
}
