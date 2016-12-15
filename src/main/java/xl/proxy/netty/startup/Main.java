package xl.proxy.netty.startup;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xl.proxy.config.Configuration;
import xl.proxy.config.ProxyConfig;
import xl.proxy.netty.cmd.CmdAssistant;
import xl.proxy.netty.cmd.CmdServer;
import xl.proxy.netty.forward.ProxyForwardServer;
import xl.proxy.netty.runtime.SystemContext;
import xl.proxy.netty.socks.SocksServer;


public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException {
		final ProxyConfig config = new Configuration<ProxyConfig>("proxy.yml", ProxyConfig.class).get();

		SystemContext.init(config);
		logger.info(config.toString());

		if(config.getMode().equals("client")) {
			CmdAssistant.getInstance().start();
			new CountDownLatch(1).await();
		} else if(config.getMode().equals("server")) {
			new CmdServer().start();
		}
		
		if (config.getProxy().equals("forward")) {
			new ProxyForwardServer().start();
		} else if (config.getProxy().equals("socks")) {
			new SocksServer().start();
		}
		
		//System.out.println(getUnsignedByte((byte)0x01));
	}
	
	static int getUnsignedByte(byte data) { // 将data字节型数据转换为0~255 (0xFF 即BYTE)。
		return data & 0x0FF;
	}

}
