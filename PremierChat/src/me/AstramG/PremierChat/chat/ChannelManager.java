package me.AstramG.PremierChat.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.AstramG.PremierChat.chat.Messenger.MessageType;
import me.AstramG.PremierChat.events.ChannelJoinEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent;
import me.AstramG.PremierChat.events.ChannelLeaveEvent.LeaveReason;
import me.AstramG.PremierChat.main.PremierChat;
import me.AstramG.PremierChat.util.UUIDFetcher;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChannelManager {
	
	private PremierChat premierChat;
	private List<Channel> channels = new ArrayList<Channel>();
	private Channel defaultChannel;
	public List<String> mutedPlayers = new ArrayList<String>();
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
	
	public void mutePlayer(final Player player, int time) {
		mutedPlayers.add(player.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(premierChat, new BukkitRunnable() {
			public void run() {
				if (mutedPlayers.contains(player.getName())) {
					mutedPlayers.remove(player.getName());
				}
			}
		}, (time * 60) * 20L);
	}
	
	public void joinNewChannel(Player player, String channelName) {
		if (getChannel(channelName).getType() == ChannelType.PERMISSION) {
			PermissionChannel permissionChannel = (PermissionChannel) getChannel(channelName);
			if (!(player.hasPermission(permissionChannel.getPermission()))) {
				premierChat.getMessenger().sendMessage(player, "You don't have the permission to join this channel!", MessageType.DANGER);
				return;
			}
		}
		UUID uuid = null;
		try {
			uuid = UUIDFetcher.getUUIDOf(player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (getChannel(channelName).getBannedPlayers().contains(uuid.toString())) {
			premierChat.getMessenger().sendMessage(player, "You are banned from this channel!", MessageType.DANGER);
			return;
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
	
	public void unbanPlayer(OfflinePlayer banned, String channelName) {
		File file = new File("PremierChannels/" + channelName + "Bans.txt");
		List<String> players = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = "";
	    	while ((line = bufferedReader.readLine()) != null) {
	    		players.add(line);
	    	}
	    	bufferedReader.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		try {
			players.remove(UUIDFetcher.getUUIDOf(banned.getName()).toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		file.delete();
		File newFile = new File("PremierChannels/" + channelName + "Bans.txt");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(newFile));
			for (String uuid : players) {
				writer.write(uuid + "\r\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void banPlayer(OfflinePlayer player, String channelName) {
		UUID uuid = null;
		try {
			uuid = UUIDFetcher.getUUIDOf(player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		File file = new File("PremierChannels/" + channelName + "Bans.txt");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(uuid.toString() + "\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (player.isOnline()) {
			if (playerChannels.get(player.getName()).getName().equalsIgnoreCase(channelName)) {
				joinNewChannel(player.getPlayer(), getDefaultChannel().getName());
			}
			premierChat.getMessenger().sendMessage(player.getPlayer(), "You have been banned from the channel " + channelName + "!", MessageType.DANGER);
		}
		getChannel(channelName).setBannedPlayers(getBannedPlayers(channelName));
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
		    FileReader fileReader = new FileReader(file);
		    BufferedReader bufferedReader = new BufferedReader(fileReader);
		    String line = "";
		    List<String> players = new ArrayList<String>();
		    while ((line = bufferedReader.readLine()) != null) {
		    	players.add(line);
		    }
		    bufferedReader.close();
		    return players;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
