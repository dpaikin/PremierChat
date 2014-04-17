package me.AstramG.PremierChat.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.AstramG.PremierChat.chat.Messenger.MessageType;
import me.AstramG.PremierChat.events.ChannelJoinEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent.LeaveReason;
import me.AstramG.PremierChat.main.PremierChat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChannelManager {
	
	private PremierChat premierChat;
	private List<Channel> channels = new ArrayList<Channel>();
	private Channel defaultChannel;
	public HashMap<String, Channel> playerChannels = new HashMap<String, Channel>();
	
	public ChannelManager(PremierChat premierChat) {
		this.premierChat = premierChat;
	}
	
	public List<Channel> getChannels() {
		return channels;
	}
	
	public void registerNewChannel(Channel channel, Boolean defaultC) {
		channels.add(channel);
		if (defaultC) this.defaultChannel = channel;
	}
	
	public Channel getDefaultChannel() {
		return defaultChannel;
	}
	
	public Channel getChannel(String channelName) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(channelName)) {
				return channel;
			}
		}
		return null;
	}
	
	public void joinNewChannel(Player player, String channelName) {
		if (getChannel(channelName).getType() == ChannelType.PERMISSION) {
			PermissionChannel permissionChannel = (PermissionChannel) getChannel(channelName);
			if (!(player.hasPermission(permissionChannel.getPermission()))) {
				premierChat.getMessenger().sendMessage(player, "You don't have the permission to join this channel!", MessageType.DANGER);
				return;
			}
		}
		if (this.playerChannels.get(player.getName()) != null) {
			Channel currentChannel = playerChannels.get(player.getName());
			Bukkit.getPluginManager().callEvent(new ChannelLeaveEvent(currentChannel, player, LeaveReason.JOIN_ANOTHER));
			List<String> currentPlayers = currentChannel.getPlayers();
			currentPlayers.remove(player.getName());
			currentChannel.setPlayers(currentPlayers);
		}
		Channel channel = premierChat.getChannelManager().getChannel(channelName);
		premierChat.getChannelManager().playerChannels.put(player.getName(), channel);
		Bukkit.getPluginManager().callEvent(new ChannelJoinEvent(channel, player));
		List<String> newPlayers = channel.getPlayers();
		newPlayers.add(player.getName());
		channel.setPlayers(newPlayers);
	}
	
	public Boolean isInSameChannelAs(Player player, Player compare) {
		return playerChannels.get(player.getName()).getName().equalsIgnoreCase(playerChannels.get(player.getName()).getName());
	}
	
	public Boolean containsChannel(String channelName) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(channelName)) return true;
		}
		return false;
	}
	
	public List<String> getBannedPlayers(String channelName) {
		File dir = new File("PremierChannels");
		if (!(dir.exists())) {
			dir.mkdir();
		}
		File file = new File(dir + "/" + channelName + "Bans.txt");
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		try {
			FileReader fr = new FileReader(file);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			List<String> banned = new ArrayList<String>();
			try {
				while ((line = br.readLine()) != null) {
					banned.add(line);
				}
				return banned; 
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
