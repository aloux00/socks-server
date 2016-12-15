/*
 * Copyright (c) 2014 The APN-PROXY Project
 *
 * The APN-PROXY Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package xl.proxy.netty.forward;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import xl.proxy.netty.cmd.CmdAssistant;

public class CmdClientForwardFrontendHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CmdClientForwardFrontendHandler.class);

	private final String remoteHost;
	private final int remotePort;

	private volatile Channel outboundChannel;

	public CmdClientForwardFrontendHandler(String remoteHost, int remotePort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		final Channel inboundChannel = ctx.channel();

        // acquire a channel from cmd
        Promise<Object> acquirePromise = CmdAssistant.getInstance().acquireBridgeChannnel();
        acquirePromise.addListener(new GenericFutureListener<Future<Object>>() {
			@Override
			public void operationComplete(Future<Object> future) throws Exception {
				if(future.isSuccess()) {
					outboundChannel = (Channel) future.get();
					outboundChannel.config().setAutoRead(false);
					List<String> names = outboundChannel.pipeline().names();
					for(String name: names) {
						if(!name.startsWith("DefaultChannelPipeline$TailContext") && !name.equals("loggingHandler")) {
							outboundChannel.pipeline().remove(name);
						}
					}
					outboundChannel.pipeline().addLast(new CmdClientForwardBackendHandler(inboundChannel));
					outboundChannel.config().setAutoRead(true);
                	//激活读取
                    inboundChannel.read();
				} else {
					logger.info("C: " + remoteHost + ":" + remotePort + ", F");
					inboundChannel.close();
				}
			}
		});
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		if (outboundChannel.isActive()) {
			outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						// was able to flush out data, start to read the next
						// chunk
						ctx.channel().read();
					} else {
						future.channel().close();
					}
				}
			});
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if (outboundChannel != null) {
			closeOnFlush(outboundChannel);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		closeOnFlush(ctx.channel());
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
