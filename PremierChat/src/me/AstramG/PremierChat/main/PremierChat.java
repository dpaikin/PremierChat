package me.AstramG.PremierChat.main;

import java.io.File;
import java.util.List;

import me.AstramG.PremierChat.chat.Channel;
import me.AstramG.PremierChat.chat.ChannelManager;
import me.AstramG.PremierChat.chat.ChannelType;
import me.AstramG.PremierChat.chat.LocalChannel;
import me.AstramG.PremierChat.chat.Messenger;
import me.AstramG.PremierChat.chat.PermissionChannel;
import me.AstramG.PremierChat.chat.UnlistedChannel;
import me.AstramG.PremierChat.commands.PremierChatCommand;
import me.AstramG.PremierChat.commands.UUIDCommands;
import me.AstramG.PremierChat.listeners.ChatListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PremierChat extends JavaPlugin {
	
	/*
	 * Todo:
	 * - Implement Local Chat - DONE
	 * - Implement Global Chat - DONE
	 * - Implement World Chat - DONE
	 * - Custom Chat Channels for those 3 chats - DONE
	 * - Chat Channels for specific PEX groups - DONE
	 * - Chat Timers - DONE
	 * - Permissions for chat channels - DONE
	 * - Price for speaking in certain channels - NOT DONE / QUESTIONABLE ADDITION
	 * - Channel Join Messages - DONE
	 * - Channel Leave Messages - IMPLEMENTATION ADDED / QUESTIONABLE ADDITION
	 * - Channel Bans - DONE
	 * - Channel Mutes - DONE
	 * - Channel Moderators - DONE
	 * - Channel Prefixes - DONE
	 * - Player Prefixes - DONE
	 * - Mentioning System - DONE
	 * - Messaging System - DONE
	 * - MySQL support - NOT DONE
	 * - MultiChannel Support - NOT DONE
	 * - Bungee support - NOT DONE
	 * - Update to UUIDs - DONE WITH BANS AND ALL CURRENT FEATURES
	 */
	
	Messenger messenger;
	ChannelManager channelManager;
	static PremierChat premierChat;
	
	public void onEnable() {
		premierChat = this;
		messenger = new Messenger(this);
		channelManager = new ChannelManager(this);
		registerEvents();
		registerCommands();
		loadServerChannels();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
	}
	
	public static PremierChat getInstance() {
		return premierChat;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
	public ChannelManager getChannelManager() {
		return channelManager;
	}
	
	/*
	 Prefixes:
	   Admin:
	     Prefix: ?7[?4Admin?7]
	   Default:
	     Prefix: ?7[?3Noob?7]
	   Players:
	     UUID:
	       Prefix: Admin
	     UUID:
	       Prefix: Admin
	 */
	
	public void loadServerChannels() {
		List<String> onlineChannels = this.getConfig().getStringList("Channels.OnlineChannels");
		String defaultChannel = this.getConfig().getString("Channels.DefaultChannel");
		for (String onlineChannel : onlineChannels) {
			String root = "Channels." + onlineChannel + ".";
			ChannelType type = ChannelType.valueOf(this.getConfig().getString(root + "type"));
			String format = this.getConfig().getString(root + "format");
			Channel channel;
			if (type == ChannelType.PERMISSION) {
				channel = new PermissionChannel(onlineChannel, format, type, this.getConfig().getString(root + "permission").replace("/dot/", "."));
			} else if (type == ChannelType.LOCAL) {
				channel = new LocalChannel(onlineChannel, format, type, this.getConfig().getDouble(root + "range"));
			} else if (type == ChannelType.UNLISTED) {
				channel = new UnlistedChannel(onlineChannel, format, type, true);
			} else {
				channel = new Channel(onlineChannel, format, type);
			}
			if (this.getConfig().getInt(root + "timer") != 0) {
				channel.setTimer(true, this.getConfig().getInt(root + "timer"));
			}
			channel.setJoinMessage(this.getConfig().getString(root + "joinMessage"));
			channel.setBannedPlayers(channelManager.getBannedPlayers(channel.getName()));
			if (!(onlineChannel.equalsIgnoreCase(defaultChannel))) {
				channelManager.registerNewChannel(channel, false);
			} else {
				channelManager.registerNewChannel(channel, true);
			}
		}
	}
	
	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
	}
	
	public void registerCommands() {
		getCommand("pc").setExecutor(new PremierChatCommand(this));
		getCommand("uuid").setExecutor(new UUIDCommands(this));
		getCommand("msg").setExecutor(getMessenger());
		getCommand("r").setExecutor(getMessenger());
	}
	
}
