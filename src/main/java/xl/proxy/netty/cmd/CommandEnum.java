package xl.proxy.netty.cmd;

public enum CommandEnum {
	WELCOME((byte) 0x00),
	INIT((byte) 0x01),
	ACQUIRE((byte) 0x10),
	RECYCLE((byte) 0x11),
	HEARTBEAT((byte) 0x80),
	FAIL((byte) 0xe0),
	RESP((byte) 0xff);
	
	byte data;
	
	private CommandEnum(byte data) {
		this.data = data;
	}
	
	public byte getByte() {
		return this.data;
	}
	
	
	public static CommandEnum valueOf(byte data) {
		for(CommandEnum cmd: CommandEnum.values()) {
			if(cmd.data == data) return cmd;
		}
		throw new RuntimeException("not found valid relay cmd");
	}
}
