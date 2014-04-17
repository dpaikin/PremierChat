package me.AstramG.PremierChat.commands;

import me.AstramG.PremierChat.main.PremierChat;
import me.AstramG.PremierChat.util.UUIDFetcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UUIDCommands implements CommandExecutor {
	
	PremierChat premierChat;
	
	public UUIDCommands(PremierChat premierChat) {
		this.premierChat = premierChat;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("uuid")) {
			if (sender.isOp()) {
				if (args.length == 1) {
					try {
						sender.sendMessage(ChatColor.GREEN + "The UUID is: " + UUIDFetcher.getUUIDOf(args[0]));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
	
}
