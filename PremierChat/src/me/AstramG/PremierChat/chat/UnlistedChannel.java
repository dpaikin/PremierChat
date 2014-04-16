package me.AstramG.PremierChat.chat;

public class UnlistedChannel extends Channel {

	Boolean unlisted;
	
	public UnlistedChannel(String name, String format, ChannelType channelType, Boolean unlisted) {
		super(name, format, channelType);
		this.unlisted = unlisted;
	}
	
	public Boolean isUnlisted() {
		return unlisted;
	}
	
	public void setUnlisted(Boolean unlisted) {
		this.unlisted = unlisted;
	}
	
}
