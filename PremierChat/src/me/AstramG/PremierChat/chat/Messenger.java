package me.AstramG.PremierChat.chat;

import me.AstramG.PremierChat.PremierChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messenger {
	
	PremierChat premierChat;
	static String pluginPrefix = "";
	static String pluginName = "";
	
	public Messenger(PremierChat premierChat) {
		this.premierChat = premierChat;
		Messenger.pluginPrefix = premierChat.getConfig().getString("PluginPrefix");
		Messenger.pluginName = premierChat.getConfig().getString("PluginName");
	}
	
	public static String getPluginPrefix() {
		return pluginPrefix;
	}
	
	public static String getPluginName() {
		return pluginName;
	}
	
	public void sendMessage(Player player, String message, MessageType type) {
		switch (type) {
		case NOTIFICATION:
			message = ChatColor.GREEN + "" + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + pluginPrefix + ChatColor.GREEN + ChatColor.BOLD + "]" + ChatColor.RESET + ChatColor.GREEN + message;
			break;
		case DANGER:
			message = ChatColor.GREEN + "" + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + pluginPrefix + ChatColor.GREEN + ChatColor.BOLD + "]" + ChatColor.RESET + ChatColor.RED + message;
			break;
		}
		player.sendMessage(message);
	}
	
	public enum MessageType {
		NOTIFICATION, DANGER;
	}
	
}
