package xl.proxy.netty.cmd;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class CmdSeverInitializer <T extends Channel> extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
		ch.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
		ch.pipeline().addLast("lengthEncoder", new LengthFieldPrepender(2));
		/*ch.pipeline().addLast("timeoutHandler", new ReadTimeoutHandler(5));*/

		ch.pipeline().addLast("cmdDecode", new CmdDecode());
		ch.pipeline().addLast("cmdHandler", 
				new CmdHandler(null, null));
	}

}
