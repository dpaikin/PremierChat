package me.AstramG.PremierChat.chat;

import java.util.ArrayList;
import java.util.List;

import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LocalChannel extends Channel {

	double range;
	
	public LocalChannel(String name, String format, ChannelType channelType, double range) {
		super(name, format, channelType);
		this.range = range;
	}
	
	public double getRange() {
		return range;
	}
	
	public void setRange(double range) {
		this.range = range;
	}
	
	public List<Player> getReceivers(Player player) {
		List<Entity> entities = player.getNearbyEntities(range, range, range);
		List<Player> nearPlayers = new ArrayList<Player>();
		for (Entity entity : entities) {
			if (entity instanceof Player) {
				Player nearPlayer = (Player) entity;
				if (PremierChat.getInstance().getChannelManager().isInSameChannelAs(player, nearPlayer)) {
					nearPlayers.add(nearPlayer);
				}
			}
		}
		return nearPlayers;
	}
	
}
