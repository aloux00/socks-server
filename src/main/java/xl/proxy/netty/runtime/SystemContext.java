package xl.proxy.netty.runtime;

import xl.proxy.config.ProxyConfig;

public class SystemContext {

	private static ProxyConfig config;
	
	public static void init(ProxyConfig config) {
		SystemContext.config = config;
	}
	
	public static ProxyConfig getProxyConfig() {
		return config;
	}
}
