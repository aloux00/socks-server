package xl.proxy.config;

public class SocketServerConfig {

	private int bossThreads;
	private int workerThreads;
	private String host;
	private int port;
	public int getBossThreads() {
		return bossThreads;
	}
	public void setBossThreads(int bossThreads) {
		this.bossThreads = bossThreads;
	}
	public int getWorkerThreads() {
		return workerThreads;
	}
	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
