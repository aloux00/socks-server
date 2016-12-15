package com.kedacom.proxy.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
	
	private static boolean isLinuxSystem = true;
	static {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("windows")) {
			// 本地是windows
			isLinuxSystem = false;
		}
	}
	
	public static boolean isLinuxSystem() {
		return isLinuxSystem;
	}
	
	/**
	 * @param hostAddress
	 * @param timeout sec
	 * @return
	 */
	public static boolean ping(String hostAddress, int timeout) {
		Process process = null;
		try {
			// this uses the exit-status of ping, where 0 is success
			String command = (isLinuxSystem())?"ping -c 4 -w " + (timeout >=0 ? timeout : 1):"ping -n 2 -w " + ((timeout >=0 ? timeout * 1000 : 1000));
			process = Runtime.getRuntime().exec(command +" "+ hostAddress);
			return (process.waitFor() == 0)?true:false;
		} catch (Exception e) {
			logger.error("Failed to get native ping status "+ hostAddress, e);
			return false;
		} finally {
			if (process != null) process.destroy();
		}
	}
	
	/**
     * 端口检测
     * 〈一句话功能简述〉
     * @param hostAddress
     * @param port
     * @param timeout milliseconds
     * @return
     * @author:     xielioo
     * @since       2016年5月17日 下午3:41:00
     * @version:    ptt1.0.0
     * @throws IOException 
     */
    public static boolean portIsOpen(String hostAddress, int port, int timeout) throws IOException {
		SocketAddress sockaddr = new InetSocketAddress(hostAddress, port);
		Socket sock = new Socket();
		sock.connect(sockaddr, timeout);
		sock.close();
		return true;
	}
    
    public static void main(String[] args) throws IOException {
    	System.out.println(System.currentTimeMillis());
    	System.out.println(ping("172.16.193.180", 300));
    	System.out.println(System.currentTimeMillis());
//		System.out.println(portIsOpen("10.20.144.110", 9900, 200));
	}
}
