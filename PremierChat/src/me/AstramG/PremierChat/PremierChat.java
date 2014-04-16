package me.AstramG.PremierChat;

import java.io.File;
import java.util.List;

import me.AstramG.PremierChat.chat.Channel;
import me.AstramG.PremierChat.chat.ChannelType;
import me.AstramG.PremierChat.chat.ChannelManager;
import me.AstramG.PremierChat.chat.LocalChannel;
import me.AstramG.PremierChat.chat.Messenger;
import me.AstramG.PremierChat.chat.PermissionChannel;
import me.AstramG.PremierChat.commands.PremierChatCommand;
import me.AstramG.PremierChat.listeners.ChatListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebeaninternal.server.core.ConfigBuilder;

public class PremierChat extends JavaPlugin {
	
	/*
	 * Todo:
	 * - Implement Local Chat - DONE
	 * - Implement Global Chat
	 * - Implement World Chat
	 * - Custom Chat Channels for those 3 chats
	 * - Chat Channels for specific PEX groups
	 * - Chat Timers
	 * - Permissions for chat channels
	 * - Price for certain channels
	 * - Channel Join Messages
	 * - Channel Leave Messages
	 * - Channel Bans
	 * - Channel Mutes
	 * - Channel Moderators
	 * - Prefixes
	 * - Mentioning System
	 * - Messaging System
	 * - MySQL support
	 */
	
	Messenger messenger;
	ChannelManager channelManager;
	static PremierChat premierChat;
	
	@Override
	public void onEnable() {
		premierChat = this;
		messenger = new Messenger(this);
		channelManager = new ChannelManager(this);
		registerEvents();
		registerCommands();
		loadChannels();
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
	
	public void loadChannels() {
		List<String> onlineChannels = this.getConfig().getStringList("Channels.OnlineChannels");
		String defaultChannel = this.getConfig().getString("Channels.DefaultChannel");
		for (String onlineChannel : onlineChannels) {
			String root = "Channels." + onlineChannel + ".";
			ChannelType type = ChannelType.valueOf(this.getConfig().getString(root + "type"));
			String format = this.getConfig().getString(root + "format");
			Channel channel;
			if (type == ChannelType.PERMISSION) {
				channel = new PermissionChannel(onlineChannel, format, type, this.getConfig().getString(root + "permission"));
			} else if (type == ChannelType.LOCAL) {
				channel = new LocalChannel(onlineChannel, format, type, this.getConfig().getDouble(root + "range"));
			} else {
				channel = new Channel(onlineChannel, format, type);
			}
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
	}
	
}
