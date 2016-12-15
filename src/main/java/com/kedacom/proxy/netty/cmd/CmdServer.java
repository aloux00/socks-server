package com.kedacom.proxy.netty.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kedacom.proxy.netty.runtime.SystemContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;

public class CmdServer extends Thread {
	private volatile boolean started = false;

	private static final Logger logger = LoggerFactory.getLogger(CmdServer.class);

	public CmdServer() {
		this.setDaemon(false);
	}

	@Override
	public void run() {
		if (started)
			return;
		started = true;
		
		lanch(); 
	}
	
	private void lanch() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(
				Runtime.getRuntime().availableProcessors());
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new CmdSeverInitializer<NioServerSocketChannel>());
			b.option(ChannelOption.SO_BACKLOG, SystemContext.getProxyConfig().getChannelConfig().getAcceptBackLog());

			b.childOption(ChannelOption.TCP_NODELAY, SystemContext.getProxyConfig().getChannelConfig().isTcpNoDelay());
			b.childOption(ChannelOption.SO_KEEPALIVE, SystemContext.getProxyConfig().getChannelConfig().isTcpKeepAlive());
			b.childOption(ChannelOption.SO_REUSEADDR, SystemContext.getProxyConfig().getChannelConfig().isReuseAddress());
			// 绑定端口，同步等待成功
			ChannelFuture future  = b.bind(SystemContext.getProxyConfig().getCmdServerConfig().getPort()).syncUninterruptibly();
			future.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						logger.info("cmd server started");
					} else {
						logger.error("cmd server failed", future.cause());
					}
				}
			});
			// 等待服务端监听端口关闭
			future.channel().closeFuture().syncUninterruptibly();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
