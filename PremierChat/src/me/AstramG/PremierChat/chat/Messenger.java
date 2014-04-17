package me.AstramG.PremierChat.chat;

import java.util.HashMap;

import me.AstramG.PremierChat.events.MessageSendEvent;
import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger implements CommandExecutor {
	
	PremierChat premierChat;
	static String pluginPrefix = "";
	static String pluginName = "";
	public HashMap<String, String> lastChat = new HashMap<String, String>();
	
	public Messenger(PremierChat premierChat) {
		this.premierChat = premierChat;
		Messenger.pluginPrefix = premierChat.getConfig().getString("PluginPrefix");
		Messenger.pluginName = premierChat.getConfig().getString("PluginName");
	}
	
	public void sendPrivateMessage(Player from, Player to, String message) {
		String messageFormatTo = premierChat.getConfig().getString("Message.Format.to");
		String messageFormatFrom = premierChat.getConfig().getString("Message.Format.from");
		to.sendMessage(ChatColor.translateAlternateColorCodes('?', messageFormatTo.replace(";from;", from.getName()).replace(";to;", to.getName()).replace(";msg;", message).replace("/fbracket/", "[").replace("/rbracket/", "]")));
		from.sendMessage(ChatColor.translateAlternateColorCodes('?', messageFormatFrom.replace(";from;", from.getName()).replace(";to;", to.getName()).replace(";msg;", message).replace("/fbracket/", "[").replace("/rbracket/", "]")));
	}
	
	public static String getPluginPrefix() {
		return pluginPrefix;
	}
	
	public static String getPluginName() {
		return pluginName;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("msg")) {
			if (!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			if (args.length >= 2) {
				if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
					Player messageTo = Bukkit.getPlayer(args[1]);
					String message = "";
					for (int i = 1; i < args.length; i ++) {
						message += args[i] + " ";
					}
					Bukkit.getPluginManager().callEvent(new MessageSendEvent(player, messageTo, message));
				}
			}
		} else if (label.equalsIgnoreCase("r")) {
			if (!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			if (lastChat.containsKey(player.getName())) {
				if (args.length >= 1) {
					String message = "";
					for (int i = 0; i < args.length; i ++) {
						message += args[i] + " ";
					}
					if (Bukkit.getOfflinePlayer(lastChat.get(player.getName())).isOnline()) {
						Player messageTo = Bukkit.getPlayer(lastChat.get(player.getName()));
						Bukkit.getPluginManager().callEvent(new MessageSendEvent(player, messageTo , message));
					} else {
						sendMessage(player, "The player you were speaking with has logged off!", MessageType.DANGER);
					}
				}
			} else {
				sendMessage(player, "You don't have anyone to reply to!", MessageType.DANGER);
			}
		}
		return true;
	}
	
	public void sendMessage(Player player, String message, MessageType type) {
		switch (type) {
		case NOTIFICATION:
			message = ChatColor.GREEN + "" + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + pluginPrefix + ChatColor.GREEN + ChatColor.BOLD + "] " + ChatColor.RESET + ChatColor.GREEN + message;
			break;
		case DANGER:
			message = ChatColor.GREEN + "" + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + pluginPrefix + ChatColor.GREEN + ChatColor.BOLD + "] " + ChatColor.RESET + ChatColor.RED + message;
			break;
		}
		player.sendMessage(message);
	}
	
	public enum MessageType {
		NOTIFICATION, DANGER;
	}
	
}
