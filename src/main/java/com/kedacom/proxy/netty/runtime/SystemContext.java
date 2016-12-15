package com.kedacom.proxy.netty.runtime;

import com.kedacom.proxy.config.ProxyConfig;

public class SystemContext {

	private static ProxyConfig config;
	
	public static void init(ProxyConfig config) {
		SystemContext.config = config;
	}
	
	public static ProxyConfig getProxyConfig() {
		return config;
	}
}
