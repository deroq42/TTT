package de.deroq.ttt.countdowns;

import de.deroq.ttt.TTT;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {

    protected final TTT ttt;
    protected BukkitTask bukkitTask;
    protected int totalSeconds;
    protected int currentSeconds;

    public Countdown(TTT ttt) {
        this.ttt = ttt;
    }

    public void onStart() {
        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                currentSeconds--;
                System.out.println("--");

                if(currentSeconds >= 1) {
                    onTick();
                    return;
                }

                onFinish();
                onStop();
            }
        }.runTaskTimer(ttt, 0, 20);
    }

    public abstract void onTick();

    public abstract void onFinish();

    public void onStop() {
        if(bukkitTask == null) {
            return;
        }

        bukkitTask.cancel();
        currentSeconds = totalSeconds;
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
