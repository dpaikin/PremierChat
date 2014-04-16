package me.AstramG.PremierChat.commands;

import java.util.ArrayList;
import java.util.List;

import me.AstramG.PremierChat.chat.Channel;
import me.AstramG.PremierChat.chat.Messenger;
import me.AstramG.PremierChat.chat.Messenger.MessageType;
import me.AstramG.PremierChat.chat.PermissionChannel;
import me.AstramG.PremierChat.chat.UnlistedChannel;
import me.AstramG.PremierChat.events.ChannelJoinEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent.LeaveReason;
import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PremierChatCommand implements CommandExecutor {
	
	private PremierChat premierChat;
	
	public PremierChatCommand(PremierChat premierChat) {
		this.premierChat = premierChat;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("pc")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + Messenger.getPluginName() + " commands:");
				sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel " + ChatColor.GRAY + "--> Channel Commands");
				sender.sendMessage(ChatColor.GREEN + "" + "  - /msg <player> <message>" + ChatColor.GRAY + "--> Sends a message to a player");
				sender.sendMessage(ChatColor.GREEN + "" + "  - /r <message>" + ChatColor.GRAY + "--> Replies the message to the last player contacted");
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("channel")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						if (args.length == 1) {
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Channel commands:");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel list " + ChatColor.GRAY + "--> Displays a list of all of the availible channels");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel join <channel>" + ChatColor.GRAY + "--> Joins the specified channel");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel info <channel>" + ChatColor.GRAY + "--> Displays information for a channel");
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
						} else if (args.length == 2) {
							if (args[1].equalsIgnoreCase("list")) {
								List<Channel> allowedChannels = new ArrayList<Channel>();
								for (Channel channel : premierChat.getChannelManager().getChannels()) {
									allowedChannels.add(channel);
									if (channel instanceof PermissionChannel) {
										if (player.hasPermission(((PermissionChannel) channel).getPermission())) allowedChannels.remove(channel);
									} else if (channel instanceof UnlistedChannel) {
										if (((UnlistedChannel) channel).isUnlisted()) allowedChannels.remove(channel);
									}
								}
								String channelString = "Channels: ";
								for (Channel channel : allowedChannels) {
									if (!(allowedChannels.indexOf(channel) + 1 == allowedChannels.size())) {
										channelString += channel.getName() + ",";
									} else {
										channelString += channel.getName();
									}
								}
								premierChat.getMessenger().sendMessage(player, channelString, MessageType.NOTIFICATION);
							}
						} else if (args.length == 3) {
							if (premierChat.getChannelManager().containsChannel(args[2])) {
								if (args[1].equalsIgnoreCase("join")) {
									String channelToJoin = args[2];
									Channel currentChannel = premierChat.getChannelManager().playerChannels.get(player.getName());
									Bukkit.getPluginManager().callEvent(new ChannelLeaveEvent(currentChannel, player, LeaveReason.JOIN_ANOTHER));
									List<String> currentPlayers = currentChannel.getPlayers();
									currentPlayers.remove(player.getName());
									currentChannel.setPlayers(currentPlayers);
									Channel channel = premierChat.getChannelManager().getChannel(channelToJoin);
									premierChat.getChannelManager().playerChannels.put(player.getName(), channel);
									Bukkit.getPluginManager().callEvent(new ChannelJoinEvent(channel, player));
									List<String> newPlayers = channel.getPlayers();
									newPlayers.add(player.getName());
									channel.setPlayers(newPlayers);
								} else if (args[1].equalsIgnoreCase("info")) {
									String channelToDisplay = args[2];
									Channel channel = premierChat.getChannelManager().getChannel(channelToDisplay);
									premierChat.getMessenger().sendMessage(player, "Channel Type: " + channel.getType().toString(), MessageType.NOTIFICATION);
									premierChat.getMessenger().sendMessage(player, "Players: " + channel.getPlayers().size(), MessageType.NOTIFICATION);
									//premierChat.getMessenger().sendMessage(player, "Prefix: " + channel.getPrefix(), MessageType.NOTIFICATION);
								}
							} else {
								player.sendMessage(ChatColor.RED + "Couldn't find the specified channel or incorrect arguments!");
							}
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can perform channel commands!");
					}
				}
			}
		}
		return true;
	}
}
