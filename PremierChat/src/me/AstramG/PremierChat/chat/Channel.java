package me.AstramG.PremierChat.chat;

import java.util.ArrayList;
import java.util.List;

import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Channel {
	
	private String channelName = "";
	private String format = "";
	private ChannelType channelType;
	private List<String> players = new ArrayList<String>();
	private List<String> bannedPlayers = new ArrayList<String>();
	private Boolean hasTimer;
	private int timer = 0;
	private List<String> playerTimer = new ArrayList<String>();
	private String joinMessage = "";
	
	public Channel(String name, String format, ChannelType channelType) {
		this.channelName = name;
		this.channelType = channelType;
		this.format = ChatColor.translateAlternateColorCodes('?', format);
		hasTimer = false;
		timer = 0;
		joinMessage = ChatColor.GREEN + "You've joined " + name + "!";
	}
	
	public void setJoinMessage(String joinMessage) {
		this.joinMessage = joinMessage;
	}
	
	public String getJoinMessage() {
		return this.joinMessage;
	}
	
	public String getName() {
		return channelName;
	}
	
	public void setTimer(Boolean hasTimer, int time) {
		this.hasTimer = hasTimer;
		this.timer = time;
	}
	
	public Boolean hasTimer() {
		return hasTimer;
	}
	
	public int getTime() {
		return timer;
	}
	
	public void addPlayerToTimer(final String playerName) {
		playerTimer.add(playerName);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PremierChat.getInstance(), new BukkitRunnable() {
			public void run() {
				playerTimer.remove(playerName);
			}
		}, timer * 20L);
	}
	
	public Boolean canSpeak(Player player) {
		return !playerTimer.contains(player.getName());
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = ChatColor.translateAlternateColorCodes('?', format);
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
