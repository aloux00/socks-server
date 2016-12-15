package xl.proxy.netty.cmd;

public class Command {

	private final CommandEnum cmd;
	private final Integer no;
	
	public Command(CommandEnum cmd, Integer no) {
		this.cmd = cmd;
		this.no = no;
	}
	
	public CommandEnum getCmd() {
		return cmd;
	}
	public Integer getNo() {
		return no;
	}
	
	public boolean isValid() {
		return this.cmd != null && this.no != null;
	}
}
