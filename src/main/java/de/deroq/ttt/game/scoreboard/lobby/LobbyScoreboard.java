package de.deroq.ttt.game.scoreboard.lobby;

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

public class LobbyScoreboard extends GameScoreboard {

    public LobbyScoreboard(TTT ttt, GamePlayer gamePlayer) {
        super(ttt, gamePlayer);
    }

    @Override
    public void setScoreboard() {
        Team mapTeam = scoreboard.registerNewTeam("map");
        mapTeam.setPrefix("§fMap: ");
        mapTeam.addEntry("§e");
        mapTeam.setSuffix("§e" + ttt.getGameManager().getCurrentGameMap().getMuid());

        objective.getScore("§5").setScore(3);
        objective.getScore("§e").setScore(2);
        objective.getScore("§fGröße: §b" + ttt.getGameManager().MAX_PLAYERS + "x1").setScore(1);
        objective.getScore("§a").setScore(0);

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
        Team mapTeam = scoreboard.getTeam("map");
        mapTeam.setPrefix("§fMap: ");
        mapTeam.setSuffix("§e" + ttt.getGameManager().getCurrentGameMap().getMuid());
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
