package xl.proxy.config;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ProxyConfig {
	private String proxy;
	private String mode;
	private SocketServerConfig cmdServerConfig;
	private SocketServerConfig forwardServerConfig;
	private SocketServerConfig socksServerConfig;
	private ChannelConfig channelConfig;
	private ThreadPoolConfig threadPoolConfig;
	
	
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public SocketServerConfig getCmdServerConfig() {
		return cmdServerConfig;
	}
	public void setCmdServerConfig(SocketServerConfig cmdServerConfig) {
		this.cmdServerConfig = cmdServerConfig;
	}
	public SocketServerConfig getForwardServerConfig() {
		return forwardServerConfig;
	}
	public void setForwardServerConfig(SocketServerConfig forwardServerConfig) {
		this.forwardServerConfig = forwardServerConfig;
	}
	public SocketServerConfig getSocksServerConfig() {
		return socksServerConfig;
	}
	public void setSocksServerConfig(SocketServerConfig socksServerConfig) {
		this.socksServerConfig = socksServerConfig;
	}
	public ChannelConfig getChannelConfig() {
		return channelConfig;
	}
	public void setChannelConfig(ChannelConfig channelConfig) {
		this.channelConfig = channelConfig;
	}
	public ThreadPoolConfig getThreadPoolConfig() {
		return threadPoolConfig;
	}
	public void setThreadPoolConfig(ThreadPoolConfig threadPoolConfig) {
		this.threadPoolConfig = threadPoolConfig;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
