package de.deroq.ttt.game.scoreboard;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author deroq
 * @since 08.07.2022
 */

public abstract class GameScoreboard {

    protected final TTT ttt;
    protected final GamePlayer gamePlayer;
    protected final Scoreboard scoreboard;
    protected final Objective objective;

    public GameScoreboard(TTT ttt, GamePlayer gamePlayer) {
        this.ttt = ttt;
        this.gamePlayer = gamePlayer;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("ttt", "dummy");

        objective.setDisplayName("§f§lGOMMEHD.NET");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public abstract void setScoreboard();

    public abstract void setTablist();

    public abstract void updateScoreboard();

    public abstract void updateTablist();

    public void destroy() {
        gamePlayer.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        gamePlayer.setGameScoreboard(null);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }
}
