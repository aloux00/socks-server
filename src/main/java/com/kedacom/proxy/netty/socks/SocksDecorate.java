package com.kedacom.proxy.netty.socks;

import java.util.List;

import io.netty.channel.Channel;

public class SocksDecorate {
	
	private static SocksServerInitializer<Channel> decorator = new SocksServerInitializer<Channel>();
	
	public static void decorate(Channel channel) throws Exception {
		channel.config().setAutoRead(false);
		List<String> names = channel.pipeline().names();
		decorator.initChannel(channel);
		for(String name: names) {
			if(!name.startsWith("DefaultChannelPipeline$TailContext") && !name.equals("loggingHandler")) {
				channel.pipeline().remove(name);
			}
		}
		channel.config().setAutoRead(true);
	}
}
