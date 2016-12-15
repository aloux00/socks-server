package com.kedacom.proxy.netty.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kedacom.proxy.netty.runtime.SystemContext;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class CmdClient {

	private static final Logger logger = LoggerFactory.getLogger(CmdClient.class);
	
	public Promise<Object> connect(final Command connect) {
		final Bootstrap b = new Bootstrap();
		final EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		final Promise<Object> promise = workerGroup.next().newPromise();
		Thread clientThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					b.group(workerGroup).channel(NioSocketChannel.class)
							.handler(new ChannelInitializer<Channel>() {
								@Override
								protected void initChannel(Channel ch) throws Exception {
									ch.pipeline().addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
									ch.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
									ch.pipeline().addLast("lengthEncoder", new LengthFieldPrepender(2));
									ch.pipeline().addLast("cmdDecode", new CmdDecode());
									ch.pipeline().addLast("cmdHandler", 
											new CmdHandler(connect, promise));
								}
							});
					// 同步等待成功
					ChannelFuture future  = b.connect(
							SystemContext.getProxyConfig().getCmdServerConfig().getHost(), 
							SystemContext.getProxyConfig().getCmdServerConfig().getPort())
								.syncUninterruptibly();
					future.addListener(new GenericFutureListener<ChannelFuture>() {
						@Override
						public void operationComplete(final ChannelFuture future) throws Exception {
							if(future.isSuccess()) {
								logger.info("cmd client started");
								
							} else {
								logger.error("cmd client failed", future.cause());
							}
						}
					});
					// 等待服务端监听端口关闭
					future.channel().closeFuture().syncUninterruptibly();
				} finally {
					workerGroup.shutdownGracefully();
				}
			}
		});
		clientThread.setDaemon(true);
		clientThread.start();
		
		return promise;
	}
}
