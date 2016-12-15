package com.kedacom.proxy.netty.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kedacom.proxy.netty.runtime.SystemContext;
import com.kedacom.proxy.netty.socks.SocksDecorate;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class CmdHandler extends SimpleChannelInboundHandler<Command> {

	private static final Logger logger = LoggerFactory.getLogger(CmdHandler.class);

	private final Command initCmd;
	private final Promise<Object> initPromise;
	private boolean cmd = false;

	public CmdHandler(Command initCmd, Promise<Object> initPromise) {
		if(initCmd != null && CommandEnum.INIT.equals(initCmd.getCmd())) {
			this.cmd = true;
		}
		this.initCmd = initCmd;
		this.initPromise = initPromise;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		if (initPromise != null) {
			initPromise.setSuccess(ctx.channel());
		}
		if (initCmd == null)
			return;
		if (!CommandEnum.ACQUIRE.equals(initCmd.getCmd())) {
			CmdAssistant.getInstance().writeAndFlushCommand(ctx.channel(), initCmd);
		} else {
			CmdAssistant.getInstance().writeAndFlushCommand(ctx.channel(),
					new Command(CommandEnum.RESP, initCmd.getNo()));
			if (SystemContext.getProxyConfig().getProxy().equals("socks")) {
				// 作为转发通道
				SocksDecorate.decorate(ctx.channel());
			}
		}
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Command msg) throws Exception {
		switch (msg.getCmd()) {
		case INIT:
			CmdAssistant.getInstance().init(ctx.channel());
			cmd = true;
			break;
		case ACQUIRE:
			CmdAssistant.getInstance().buildBridge(msg).addListener(new GenericFutureListener<Future<Object>>() {
				@Override
				public void operationComplete(Future<Object> future) throws Exception {
					if (!future.isSuccess()) {
						logger.error("received acquire bridge cmd,but build failed");
						CmdAssistant.getInstance().writeAndFlushCommand(ctx.channel(),
								new Command(CommandEnum.FAIL, msg.getNo()));
					}
				}
			});
			break;
		case HEARTBEAT:
			CmdAssistant.getInstance().writeAndFlushCommand(ctx.channel(), new Command(CommandEnum.RESP, msg.getNo()));
			break;
		case FAIL:
			logger.info("command " + msg.getNo() + " failed");
			Promise<Object> promise = CmdAssistant.getInstance().getPromise(msg.getNo());
			if (promise != null) {
				promise.setFailure(null);
			}
			break;
		case RESP:
			logger.info("command " + msg.getNo() + " success");
			promise = CmdAssistant.getInstance().getPromise(msg.getNo());
			if (promise != null) {
				promise.setSuccess(ctx.channel());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		logger.info("cmd channnel inactive::" + ctx.channel());
		if(cmd) {
			//重连
			logger.error("CMD CHANNEL DOWN, SERVICE STOPED UNTIL RECONNECTED !!!");
			CmdAssistant.getInstance().start();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("", cause);
		if (initPromise != null && !initPromise.isDone()) {
			initPromise.setFailure(cause);
		}
	}

}
