package com.kedacom.proxy.config;

public class ChannelConfig {

	private boolean tcpNoDelay;
	private int tcpSendBufferSize;
	private int tcpReceiveBufferSize;
	private boolean tcpKeepAlive;
	private int soLinger;
	private boolean reuseAddress;
	private int acceptBackLog;
	
	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}
	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}
	public int getTcpSendBufferSize() {
		return tcpSendBufferSize;
	}
	public void setTcpSendBufferSize(int tcpSendBufferSize) {
		this.tcpSendBufferSize = tcpSendBufferSize;
	}
	public int getTcpReceiveBufferSize() {
		return tcpReceiveBufferSize;
	}
	public void setTcpReceiveBufferSize(int tcpReceiveBufferSize) {
		this.tcpReceiveBufferSize = tcpReceiveBufferSize;
	}
	public boolean isTcpKeepAlive() {
		return tcpKeepAlive;
	}
	public void setTcpKeepAlive(boolean tcpKeepAlive) {
		this.tcpKeepAlive = tcpKeepAlive;
	}
	public int getSoLinger() {
		return soLinger;
	}
	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}
	public boolean isReuseAddress() {
		return reuseAddress;
	}
	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}
	public int getAcceptBackLog() {
		return acceptBackLog;
	}
	public void setAcceptBackLog(int acceptBackLog) {
		this.acceptBackLog = acceptBackLog;
	}
	
}

