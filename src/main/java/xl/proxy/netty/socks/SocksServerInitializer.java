package xl.proxy.netty.socks;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;

public class SocksServerInitializer<T extends Channel> extends ChannelInitializer<Channel> {

    private final SocksMessageEncoder socksMessageEncoder = new SocksMessageEncoder();
    private final SocksServerHandler socksServerHandler = new SocksServerHandler();
    
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new SocksInitRequestDecoder());
		ch.pipeline().addLast(socksMessageEncoder);
		ch.pipeline().addLast(socksServerHandler);
	}

}
