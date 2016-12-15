package xl.proxy.netty.cmd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import xl.proxy.netty.runtime.SystemContext;

public class CmdAssistant {

	private static final Logger logger = LoggerFactory.getLogger(CmdAssistant.class);

	private final Map<Integer, Promise<Object>> taskFutures = new ConcurrentHashMap<Integer, Promise<Object>>();

	/**
	 * 序号
	 */
	private AtomicInteger NO = new AtomicInteger(0);
	/**
	 * 最大序号，循环
	 */
	private int MAX_NO = 256;

	private int COMMANG_LEN = 2;

	private Channel cmdChannel;

	private boolean initialized = false;

	private static CmdAssistant instance = new CmdAssistant();
	
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public static CmdAssistant getInstance() {
		return instance;
	}

	public void start() {
		if ("client".equals(SystemContext.getProxyConfig().getMode())) {
			Command connect = new Command(CommandEnum.INIT, getAvailableNo());
			new CmdClient().connect(connect).addListener(new GenericFutureListener<Future<Object>>() {
				@Override
				public void operationComplete(Future<Object> future) throws Exception {
					if(future.isSuccess()) {
						cmdChannel = (Channel) future.getNow();
						initialized = true;
						cmdChannel.eventLoop().scheduleWithFixedDelay(new Runnable() {
							public void run() {
								writeAndFlushCommand(cmdChannel, new Command(CommandEnum.HEARTBEAT, getAvailableNo()));
							}
						}, 0, 3, TimeUnit.SECONDS);
					} else {
						executor.schedule(new Runnable() {
							@Override
							public void run() {
								start();
							}
						}, 5, TimeUnit.SECONDS);
					}
				}
			});
		}
	}

	public void init(Channel cmdChannel) {
		if (!cmdChannel.isActive()) {
			logger.error("cmd channel inactive, can not initialize cmd system !!!");
			return;
		}
		if ("server".equals(SystemContext.getProxyConfig().getMode())) {
			if(this.cmdChannel != null) {
				this.cmdChannel.close();
			}
			this.cmdChannel = cmdChannel;
			initialized = true;
		}
	}
	
	public Promise<Object> getPromise(Integer no) {
		return taskFutures.get(no);
	}

	public Promise<Object> acquireBridgeChannnel() {
		if (!initialized) {
			throw new RuntimeException("cmd system not initialized !!!");
		}
		if (!"server".equals(SystemContext.getProxyConfig().getMode())) {
			throw new RuntimeException("illegal request, acquire bridge from server !!!");
		}
		ByteBuf encoded = cmdChannel.alloc().buffer(commandLength());
		final Integer no = NO.getAndIncrement();
		encoded.writeBytes(buildCommandBytes(new Command(CommandEnum.ACQUIRE, no)));
		logger.info("ACQUIRE cmd begin, no:" + no);
		final Promise<Object> promise = cmdChannel.eventLoop().newPromise();
		if (this.cmdChannel.isActive()) {
			this.cmdChannel.writeAndFlush(encoded).addListener(new GenericFutureListener<ChannelFuture>() {
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						taskFutures.put(no, promise);
					} else {
						logger.error("send command failed, close cmd channnel !!!");
						promise.setFailure(future.cause());
						CmdAssistant.this.cmdChannel.close();
					}
				}
			});
			return promise;
		} else {
			this.cmdChannel.close();
			logger.error("cmd channel inactive, cannot send command !!!");
		}
		return null;
	}
	
	public Promise<Object> buildBridge(Command acquireCmd) {
		if(acquireCmd == null || !acquireCmd.isValid()) {
			throw new IllegalArgumentException("acquire cmd not found or invalid");
		}
		if ("client".equals(SystemContext.getProxyConfig().getMode())) {
			return new CmdClient().connect(acquireCmd);
		}
		return null;
	}

	public int commandLength() {
		return instance.COMMANG_LEN;
	}
	
	public Integer getAvailableNo() {
		return instance.NO.getAndIncrement();
	}

	public byte[] buildCommandBytes(Command command) {
		if (command == null) {
			throw new IllegalArgumentException("command can not be null !!!");
		}
		return new byte[] { command.getCmd().getByte(), (byte) (command.getNo() % instance.MAX_NO) };
	}
	
	public Future<?> writeAndFlushCommand(Channel channel, Command cmd) {
		if(channel.isActive()) {
			ByteBuf encoded = channel.alloc().buffer(commandLength());
			encoded.writeBytes(buildCommandBytes(cmd));
			return channel.writeAndFlush(encoded);
		} else {
			return channel.eventLoop().newPromise().setFailure(new RuntimeException("channel inactive"));
		}
	}
}
