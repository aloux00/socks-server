package xl.proxy.netty.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class CmdDecode extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(CmdDecode.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		Command cmd = null;
		cmd = decode(msg);

		if (cmd != null) {
			ctx.fireChannelRead(cmd);
			return;
		} else {
			logger.error("error msg, not a valid command !!!");
		}
	}

	/**
	 * 解码 relay cmd
	 * 
	 * @param msg
	 * @return
	 */
	private Command decode(Object msg) {
		Command cmd = null;
		if (msg instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) msg;
			ByteBuf copybuf = buf.copy().retain();
			try {
				byte[] data = null;
				if (copybuf.hasArray()) {
					data = copybuf.array();
				} else {
					if (copybuf.readableBytes() == CmdAssistant.getInstance().commandLength()) {
						data = new byte[] { copybuf.readByte(),
								copybuf.readByte() };
					}
				}
				if (data != null) {
					try {
						CommandEnum cmdEnum = CommandEnum.valueOf(data[0]);
						cmd = new Command(cmdEnum, getUnsignedByte(data[1]));
					} catch (Exception e) {
						cmd = null;
					}
				}
			} finally {
				ReferenceCountUtil.release(copybuf);
			}
		}
		return cmd;
	}

	private int getUnsignedByte(byte data) { // 将data字节型数据转换为0~255 (0xFF 即BYTE)。
		return data & 0x0FF;
	}
}
