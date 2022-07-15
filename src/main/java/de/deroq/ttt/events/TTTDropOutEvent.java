package de.deroq.ttt.events;

import de.deroq.ttt.game.models.GamePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author deroq
 * @since 15.07.2022
 */

public class TTTDropOutEvent extends Event {

    public static final HandlerList HANDLERS = new HandlerList();
    private final GamePlayer gamePlayer;

    public TTTDropOutEvent(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
