package com.kedacom.proxy.netty.manager;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public class RelayChannelProxy {

	private Channel channel;
	
	private List<ChannelHandler> otherHandlers = new ArrayList<ChannelHandler>();
	
	public RelayChannelProxy(Channel channel) {
		this.channel = channel;
	}
	
	public synchronized RelayChannelProxy addChannelHandler(ChannelHandler channelHandler) {
		this.channel.pipeline().addLast(channelHandler);
		this.otherHandlers.add(channelHandler);
		return this;
	}
	
	public Channel getChannel() {
		return channel;
	}
}
