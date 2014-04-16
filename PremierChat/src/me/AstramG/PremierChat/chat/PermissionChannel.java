package me.AstramG.PremierChat.chat;

public class PermissionChannel extends Channel {
	
	String permission = "";
	
	public PermissionChannel(String name, String format, ChannelType channelType, String permission) {
		super(name, format, channelType);
		this.permission = permission;
	}
	
	public String getPermission() {
		return permission;
	}
	
}
