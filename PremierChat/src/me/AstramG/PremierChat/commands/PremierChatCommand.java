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
				sender.sendMessage(ChatColor.GREEN + "" + "  - /msg <player> <message> " + ChatColor.GRAY + "--> Sends a message to a player");
				sender.sendMessage(ChatColor.GREEN + "" + "  - /r <message> " + ChatColor.GRAY + "--> Replies the message to the last player contacted");
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("channel")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						String rootPerm = premierChat.getConfig().getString("Admin.Permission").replace("/dot/", ".");
						if (args.length == 1) {
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Channel commands:");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel list " + ChatColor.GRAY + "--> Displays a list of all of the availible channels");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel join <channel> " + ChatColor.GRAY + "--> Joins the specified channel");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel info <channel> " + ChatColor.GRAY + "--> Displays information for a channel");
							sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin " + ChatColor.GRAY + "--> Displays the administrative commands!");
							sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
						} else if (args.length >= 2 && args[1].equalsIgnoreCase("admin") && !player.hasPermission(rootPerm + ".view")) {
							premierChat.getMessenger().sendMessage(player, "You are not allowed to do that!", MessageType.DANGER);
							return true;
						} else if (args.length == 2) {
							if (args[1].equalsIgnoreCase("list")) {
								List<Channel> allowedChannels = new ArrayList<Channel>();
								for (Channel channel : premierChat.getChannelManager().getChannels()) {
									allowedChannels.add(channel);
									if (channel instanceof PermissionChannel) {
										if (!(player.hasPermission(((PermissionChannel) channel).getPermission()))) allowedChannels.remove(channel);
									} else if (channel instanceof UnlistedChannel) {
										if (((UnlistedChannel) channel).isUnlisted() && !player.isOp()) allowedChannels.remove(channel);
									}
								}
								String channelString = "Channels: ";
								for (Channel channel : allowedChannels) {
									if (!(allowedChannels.indexOf(channel) + 1 == allowedChannels.size())) {
										channelString += channel.getName() + ", ";
									} else {
										channelString += channel.getName();
									}
								}
								premierChat.getMessenger().sendMessage(player, channelString, MessageType.NOTIFICATION);
							} else if (args[1].equalsIgnoreCase("admin")) {
								sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
								sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Admin commands:");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin mute <player> <min> " + ChatColor.GRAY + "--> Mutes the specified player for a certain time");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin unmute <player> " + ChatColor.GRAY + "--> Umutes the specified player");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin ban <channel> <player> " + ChatColor.GRAY + "--> Bans the specified player from a channel");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin pardon <player> <channel> " + ChatColor.GRAY + "--> Unbans the specified player from a channel");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin kick <player> " + ChatColor.GRAY + "--> Kicks the specified player from their channel");
								sender.sendMessage(ChatColor.GREEN + "" + "  - /pc channel admin move <player> <channel> " + ChatColor.GRAY + "--> Moves the specified player to a different channel");
								sender.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------" + ChatColor.GREEN + ChatColor.STRIKETHROUGH + "----------" + ChatColor.RED + ChatColor.STRIKETHROUGH + "----------");
							}
						} else if (args.length == 3) {
							if (premierChat.getChannelManager().containsChannel(args[2])) {
								if (args[1].equalsIgnoreCase("join")) {
									String channelToJoin = args[2];
									premierChat.getChannelManager().joinNewChannel(player, channelToJoin);
								} else if (args[1].equalsIgnoreCase("info")) {
									String channelToDisplay = args[2];
									Channel channel = premierChat.getChannelManager().getChannel(channelToDisplay);
									premierChat.getMessenger().sendMessage(player, "Channel Type: " + channel.getType().toString(), MessageType.NOTIFICATION);
									premierChat.getMessenger().sendMessage(player, "Players: " + channel.getPlayers().size(), MessageType.NOTIFICATION);
									premierChat.getMessenger().sendMessage(player, "Format:  " + channel.getFormat(), MessageType.NOTIFICATION);
								}
							} else {
								player.sendMessage(ChatColor.RED + "Couldn't find the specified channel or incorrect arguments!");
							}
						} else if (args.length == 4) {
							if (args[1].equalsIgnoreCase("admin")) {
								if (args[2].equalsIgnoreCase("unmute")) {
									if (player.hasPermission(rootPerm + ".unmute"))
										premierChat.getChannelManager().mutedPlayers.remove(args[3]);
								} else if (args[2].equalsIgnoreCase("kick")) {
									if (player.hasPermission(rootPerm + ".kick")) {
										premierChat.getMessenger().sendMessage(Bukkit.getPlayer(args[3]), "You have been kicked from the channel by " + player.getName() + "!", MessageType.DANGER);
										premierChat.getChannelManager().joinNewChannel(Bukkit.getPlayer(args[3]), premierChat.getChannelManager().getDefaultChannel().getName());
									}
								}
							}
						} else if (args.length == 5) {
							if (args[1].equalsIgnoreCase("admin")) {
								if (args[2].equalsIgnoreCase("mute")) {
									if (player.hasPermission(rootPerm + ".mute")) {
										Player muted = Bukkit.getPlayer(args[3]);
										int time = Integer.parseInt(args[4]);
										premierChat.getChannelManager().mutePlayer(muted, time);
									}
								} else if (args[2].equalsIgnoreCase("move")) {
									if (player.hasPermission(rootPerm + ".move")) {
										Player playerToMove = Bukkit.getPlayer(args[3]);
										Channel channel = premierChat.getChannelManager().getChannel(args[4]);
										Channel currentChannel = premierChat.getChannelManager().playerChannels.get(playerToMove.getName());
										Bukkit.getPluginManager().callEvent(new ChannelLeaveEvent(currentChannel, playerToMove, LeaveReason.JOIN_ANOTHER));
										List<String> currentPlayers = currentChannel.getPlayers();
										currentPlayers.remove(playerToMove.getName());
										currentChannel.setPlayers(currentPlayers);
										
										premierChat.getChannelManager().playerChannels.put(playerToMove.getName(), channel);
										Bukkit.getPluginManager().callEvent(new ChannelJoinEvent(channel, playerToMove));
										List<String> newPlayers = channel.getPlayers();
										newPlayers.add(playerToMove.getName());
										channel.setPlayers(newPlayers);
										premierChat.getMessenger().sendMessage(playerToMove, "You have been moved!", MessageType.NOTIFICATION);
									}
								}
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