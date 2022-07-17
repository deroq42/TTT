package de.deroq.ttt.game.scoreboard.ingame;

import de.deroq.ttt.TTT;
import de.deroq.ttt.game.models.GamePlayer;
import de.deroq.ttt.game.scoreboard.GameScoreboard;
import de.deroq.ttt.game.models.Role;
import de.deroq.ttt.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;

/**
 * @author deroq
 * @since 08.07.2022
 */

public class IngameScoreboard extends GameScoreboard {

    public IngameScoreboard(TTT ttt, GamePlayer gamePlayer) {
        super(ttt, gamePlayer);
        Bukkit.getScheduler().runTaskTimer(ttt, this::updateScoreboard, 0, 20);
    }

    @Override
    public void setScoreboard() {
        Team pointsTeam = scoreboard.registerNewTeam("points");
        pointsTeam.setPrefix("§fPunkte: ");
        pointsTeam.addEntry("§d");
        pointsTeam.setSuffix("§d0"); //Add points soon

        Team playersTeam = scoreboard.registerNewTeam("players");
        playersTeam.setPrefix("§fSpieler: ");
        playersTeam.addEntry("§3");
        playersTeam.setSuffix("§3" + ttt.getGameManager().getAlive().size());

        long millis = (ttt.getGameManager().getCurrentTimer().getTotalSeconds() - ttt.getGameManager().getCurrentTimer().getCurrentSeconds()) * 1000;
        Team endTeam = scoreboard.registerNewTeam("end");
        endTeam.setPrefix("§fEnde: ");
        endTeam.addEntry("§e");
        endTeam.setSuffix("§e" + BukkitUtils.formatTime(millis) + " Minuten");

        objective.getScore("§5").setScore(5);
        objective.getScore("§3").setScore(3);
        objective.getScore("§fMap: §b" + ttt.getGameManager().getCurrentGameMap().getMuid()).setScore(3);
        objective.getScore("§e").setScore(1);
        objective.getScore("§7").setScore(0);

        if (gamePlayer.getRole() != Role.INNOCENT) {
            objective.getScore("§d").setScore(4);
        }

        gamePlayer.setGameScoreboard(this);
        gamePlayer.getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public void setTablist() {
        Arrays.stream(Role.values()).forEach(role -> {
            String name = role.getName();
            String colorCode = role.getColorCode();
            Team team = scoreboard.registerNewTeam("tab-" + name);

            team.setColor(ChatColor.getByChar(colorCode.charAt(1)));
        });

        Team spectatorTeam = scoreboard.registerNewTeam("tab-spec");
        spectatorTeam.setColor(ChatColor.GRAY);

        ttt.getGameManager().getGamePlayers().forEach(gamePlayers -> {
            Player player = gamePlayers.getPlayer();
            Role role = gamePlayers.getRole();

            if (role == null) {
                spectatorTeam.addEntry(player.getName());
                player.setDisplayName(spectatorTeam.getPrefix() + player.getName());
            } else {
                if(role == Role.TRAITOR && gamePlayer.getRole() != Role.TRAITOR) {
                    role = Role.INNOCENT;
                }

                String name = role.getName();
                String colorCode = role.getColorCode();

                scoreboard.getTeam("tab-" + name).addEntry(player.getName());
                player.setDisplayName(colorCode + player.getName());
            }
        });
    }

    @Override
    public void updateScoreboard() {
        Team pointsTeam = scoreboard.getTeam("points");
        Team playersTeam = scoreboard.getTeam("players");
        Team endTeam = scoreboard.getTeam("end");

        if(pointsTeam == null || playersTeam == null || endTeam == null) {
            return;
        }

        pointsTeam.setPrefix("§fPunkte: ");
        pointsTeam.setSuffix("§d0"); //Add points soon

        playersTeam.setPrefix("§fSpieler: ");
        playersTeam.setSuffix("§3" + ttt.getGameManager().getAlive().size());

        long millis = (ttt.getGameManager().getCurrentTimer().getTotalSeconds() - ttt.getGameManager().getCurrentTimer().getCurrentSeconds()) * 1000;
        endTeam.setPrefix("§fEnde: ");
        endTeam.setSuffix("§e" + BukkitUtils.formatTime(millis) + " Minuten");
    }

    @Override
    public void updateTablist() {
        Team spectatorTeam = scoreboard.getTeam("tab-spec");
        if (spectatorTeam == null) {
            return;
        }

        ttt.getGameManager().getGamePlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            Role role = gamePlayer.getRole();

            if (role == null) {
                spectatorTeam.addEntry(player.getName());
                player.setDisplayName(spectatorTeam.getPrefix() + player.getName());
            } else {
                if(role == Role.TRAITOR && gamePlayer.getRole() != Role.TRAITOR) {
                    role = Role.INNOCENT;
                }

                String name = role.getName();
                String colorCode = role.getColorCode();

                scoreboard.getTeam("tab-" + name).addEntry(player.getName());
                player.setDisplayName(colorCode + player.getName());
            }
        });
    }
}
