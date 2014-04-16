package me.AstramG.PremierChat.listeners;


import java.util.List;

import me.AstramG.PremierChat.chat.Channel;
import me.AstramG.PremierChat.chat.ChannelType;
import me.AstramG.PremierChat.chat.LocalChannel;
import me.AstramG.PremierChat.events.ChannelJoinEvent;
import me.AstramG.PremierChat.main.PremierChat;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutCloseWindow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class ChatListener implements Listener {
	
	PremierChat premierchat;
	
	public ChatListener(PremierChat premierchat) {
		this.premierchat = premierchat;
	}
	
	@EventHandler
	public void onJoinChat(ChannelJoinEvent event) {
		event.getPlayer().sendMessage(ChatColor.GREEN + "You joiend: " + event.getChannel().getName());
	}
	
	@EventHandler
	public void onTabComplete(PlayerChatTabCompleteEvent event) {
		Channel channel = premierchat.getChannelManager().getDefaultChannel();
		doChannelMessage(event.getPlayer(), event.getChatMessage(), channel);
		EntityPlayer ep = ((CraftPlayer) event.getPlayer()).getHandle();
		PacketPlayOutCloseWindow packet = new PacketPlayOutCloseWindow();
		ep.playerConnection.sendPacket(packet);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		doChannelMessage(player, event.getMessage(), premierchat.getChannelManager().playerChannels.get(player.getName()));
		event.setCancelled(true);
	}
	
	public void doChannelMessage(Player player, String message, Channel channel) {
		String format = channel.getFormat().replace(";name;", player.getName());
		if (channel.getType() == ChannelType.LOCAL) { 
			LocalChannel localChannel = (LocalChannel) channel;
			List<Player> receivers = localChannel.getReceivers(player);
			for (Player receiver : receivers) {
				receiver.sendMessage(format + message);
			}
		} else if (channel.getType() == ChannelType.GLOBAL) {
			for (String receiver : channel.getPlayers()) {
				Bukkit.getPlayer(receiver).sendMessage(format + message);
			}
		} else if (channel.getType() == ChannelType.PERMISSION) {
			for (String receiver : channel.getPlayers()) {
				Bukkit.getPlayer(receiver).sendMessage(format + message);
			}
		} else if (channel.getType() == ChannelType.WORLD) {
			for (String receiver : channel.getPlayers()) {
				if (Bukkit.getPlayer(receiver).getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
					Bukkit.getPlayer(receiver).sendMessage(format + message);
				}
			}
		}
	}
	
}
