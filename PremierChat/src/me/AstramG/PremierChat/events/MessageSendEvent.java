package me.AstramG.PremierChat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MessageSendEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private String message = "";
    private Player from;
    private Player to;
 
    public MessageSendEvent(Player from, Player to, String message) {
        this.message = message;
        this.from = from;
        this.to = to;
    }
 
    public String getMessage() {
        return message;
    }
 
    public Player getTo() {
    	return to;
    }
    
    public Player getFrom() {
    	return from;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}