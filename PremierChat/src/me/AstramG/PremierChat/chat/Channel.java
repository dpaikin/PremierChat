package me.AstramG.PremierChat.chat;

import java.util.ArrayList;
import java.util.List;

import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.ChatColor;

public class Channel {
	
	private String channelName = "";
	private String format = "";
	private ChannelType channelType;
	private List<String> players = new ArrayList<String>();
	private List<String> bannedPlayers = new ArrayList<String>();
	
	public Channel(String name, String format, ChannelType channelType) {
		this.channelName = name;
		this.channelType = channelType;
		this.format = ChatColor.translateAlternateColorCodes('&', format);
		setBannedPlayers(PremierChat.getInstance().getChannelManager().getBannedPlayers(name));
	}
	
	public String getName() {
		return channelName;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = ChatColor.translateAlternateColorCodes('&', format);
	}
	
	public ChannelType getType() {
		return channelType;
	}
	
	public List<String> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public List<String> getBannedPlayers() {
		return bannedPlayers;
	}

	public void setBannedPlayers(List<String> bannedPlayers) {
		this.bannedPlayers = bannedPlayers;
	}
	
}
