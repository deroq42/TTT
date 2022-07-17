package de.deroq.ttt.game.scoreboard.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.scoreboard.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

/**
 * @author deroq
 * @since 08.07.2022
 */

public class ProtectionScoreboard extends GameScoreboard {

    public ProtectionScoreboard(TTT ttt, GamePlayer gamePlayer) {
        super(ttt, gamePlayer);
        Bukkit.getScheduler().runTaskTimer(ttt, this::updateScoreboard, 0, 20);
    }

    @Override
    public void setScoreboard() {
        Team playersTeam = scoreboard.registerNewTeam("players");
        playersTeam.setPrefix("§fSpieler: ");
        playersTeam.addEntry("§3");
        playersTeam.setSuffix("§3" + ttt.getGameManager().getAlive().size());

        Team protectionTeam = scoreboard.registerNewTeam("protection");
        protectionTeam.setPrefix("§fSchutzzeit: ");
        protectionTeam.addEntry("§e");
        protectionTeam.setSuffix("§e" + ttt.getGameManager().getCurrentTimer().getCurrentSeconds() + " Sekunden");

        objective.getScore("§5").setScore(4);
        objective.getScore("§fMap: §b" + ttt.getGameManager().getCurrentGameMap().getMuid()).setScore(3);
        objective.getScore("§3").setScore(2);
        objective.getScore("§e").setScore(1);
        objective.getScore("§4").setScore(0);

        gamePlayer.setGameScoreboard(this);
        gamePlayer.getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public void setTablist() {
        Team devTeam = scoreboard.registerNewTeam("0000Dev");
        devTeam.setPrefix("§cDev §7| ");
        devTeam.setColor(ChatColor.RED);

        Team playerTeam = scoreboard.registerNewTeam("0001Player");
        playerTeam.setColor(ChatColor.GREEN);

        Bukkit.getOnlinePlayers().forEach(players -> {
            if(players.isOp()) {
                devTeam.addEntry(players.getName());
                players.setDisplayName(devTeam.getPrefix() + players.getName());
            } else {
                playerTeam.addEntry(players.getName());
                players.setDisplayName(playerTeam.getPrefix() + players.getName());
            }
        });
    }

    @Override
    public void updateScoreboard() {
        Team playersTeam = scoreboard.getTeam("players");
        Team protectionTeam = scoreboard.getTeam("protection");

        if(playersTeam == null || protectionTeam == null) {
            return;
        }

        playersTeam.setPrefix("§fSpieler: ");
        playersTeam.setSuffix("§3" + ttt.getGameManager().getAlive().size());
        protectionTeam.setPrefix("§fSchutzzeit: ");
        protectionTeam.setSuffix("§e" + ttt.getGameManager().getCurrentTimer().getCurrentSeconds() + " Sekunden");
    }

    @Override
    public void updateTablist() {
        Team devTeam = scoreboard.getTeam("0000Dev");
        Team playerTeam = scoreboard.getTeam("0001Player");

        if(devTeam == null || playerTeam == null) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(players -> {
            if(players.isOp()) {
                devTeam.addEntry(players.getName());
                players.setDisplayName(devTeam.getPrefix() + players.getName());
            } else {
                playerTeam.addEntry(players.getName());
                players.setDisplayName(playerTeam.getPrefix() + players.getName());
            }
        });
    }
}
