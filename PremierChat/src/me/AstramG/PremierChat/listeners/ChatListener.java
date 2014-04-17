package me.AstramG.PremierChat.listeners;


import java.util.List;
import java.util.UUID;

import me.AstramG.PremierChat.chat.Channel;
import me.AstramG.PremierChat.chat.ChannelType;
import me.AstramG.PremierChat.chat.LocalChannel;
import me.AstramG.PremierChat.chat.Messenger.MessageType;
import me.AstramG.PremierChat.events.ChannelJoinEvent;
import me.AstramG.PremierChat.events.MessageSendEvent;
import me.AstramG.PremierChat.main.PremierChat;
import me.AstramG.PremierChat.util.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {
	
	PremierChat premierchat;
	
	public ChatListener(PremierChat premierchat) {
		this.premierchat = premierchat;
	}
	
	@EventHandler
	public void onMessageReceive(MessageSendEvent event) {
		premierchat.getMessenger().lastChat.put(event.getTo().getName(), event.getFrom().getName());
		premierchat.getMessenger().lastChat.put(event.getFrom().getName(), event.getTo().getName());
		premierchat.getMessenger().sendPrivateMessage(event.getFrom(), event.getTo(), event.getMessage());
	}
	
	@EventHandler
	public void onJoinChat(ChannelJoinEvent event) {
		if (!(event.getChannel().getJoinMessage() == null)) {
			premierchat.getMessenger().sendMessage(event.getPlayer(), ChatColor.translateAlternateColorCodes('?', event.getChannel().getJoinMessage()), MessageType.NOTIFICATION);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Channel channel = premierchat.getChannelManager().getDefaultChannel();
		premierchat.getChannelManager().playerChannels.put(event.getPlayer().getName(), channel);
		Bukkit.getPluginManager().callEvent(new ChannelJoinEvent(channel, event.getPlayer()));
		List<String> newPlayers = channel.getPlayers();
		newPlayers.add(event.getPlayer().getName());
		channel.setPlayers(newPlayers);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (event.getMessage().contains(p.getName())) {
				p.playSound(p.getLocation(), Sound.NOTE_PIANO, 2, 2);
			}
		}
		if (premierchat.getChannelManager().mutedPlayers.contains(player.getName())) {
			premierchat.getMessenger().sendMessage(player, "You are muted, therefore you may not speak!", MessageType.DANGER);
			event.setCancelled(true);
			return;
		}
		if (premierchat.getChannelManager().playerChannels.get(player.getName()).canSpeak(player)) {
			doChannelMessage(player, event.getMessage(), premierchat.getChannelManager().playerChannels.get(player.getName()));
		} else {
			Channel channel = premierchat.getChannelManager().playerChannels.get(player.getName());
			premierchat.getMessenger().sendMessage(player, "You must wait " + channel.getTime() + " seconds to speak again!", MessageType.DANGER);
		}
		event.setCancelled(true);
	}
	
	public void doChannelMessage(Player player, String message, Channel channel) {
		UUID uuid = null;
		try {
			uuid = UUIDFetcher.getUUIDOf(player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String prefix = "Default";
		if (premierchat.getConfig().getString("Prefixes.Players." + uuid.toString() + ".Prefix") != null) {
			prefix = premierchat.getConfig().getString("Prefixes.Players." + uuid.toString() + ".Prefix");
		}
		String format = channel.getFormat().replace(";prefix;", ChatColor.translateAlternateColorCodes('?', premierchat.getConfig().getString("Prefixes." + prefix + ".prefix").replace("none",  ""))).replace(";name;", player.getName()).replace(";msg;", message).replace("/fbracket/", "[").replace("/rbracket/", "]");
		if (channel.getType() == ChannelType.LOCAL) { 
			LocalChannel localChannel = (LocalChannel) channel;
			List<Player> receivers = localChannel.getReceivers(player);
			for (Player receiver : receivers) {
				receiver.sendMessage(format);
			}
			player.sendMessage(format);
		} else if (channel.getType() == ChannelType.GLOBAL) {
			for (String receiver : channel.getPlayers()) {
				Bukkit.getPlayer(receiver).sendMessage(format);
			}
		} else if (channel.getType() == ChannelType.PERMISSION) {
			for (String receiver : channel.getPlayers()) {
				Bukkit.getPlayer(receiver).sendMessage(format);
			}
		} else if (channel.getType() == ChannelType.UNLISTED) {
			for (String receiver : channel.getPlayers()) {
				Bukkit.getPlayer(receiver).sendMessage(format);
			}
		} else if (channel.getType() == ChannelType.WORLD) {
			for (String receiver : channel.getPlayers()) {
				if (Bukkit.getPlayer(receiver).getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
					Bukkit.getPlayer(receiver).sendMessage(format);
				}
			}
		}
		if (channel.hasTimer() && !player.isOp()) {
			channel.addPlayerToTimer(player.getName());
		}
		System.out.println("Channel: " + channel.getName() + " | Player: " + player.getName() + "> " + message);
	}
	
}
