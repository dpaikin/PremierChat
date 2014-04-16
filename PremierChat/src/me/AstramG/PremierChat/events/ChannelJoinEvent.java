package me.AstramG.PremierChat.events;

import me.AstramG.PremierChat.chat.Channel;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ChannelJoinEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private Channel channel;
    private Player player;
 
    public ChannelJoinEvent(Channel channel, Player player) {
        this.channel = channel;
        this.player = player;
    }
 
    public Channel getChannel() {
        return channel;
    }
 
    public Player getPlayer() {
    	return player;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}