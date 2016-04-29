package KaraFisher;

import org.powerbot.script.rt6.ClientContext;

public abstract class State_Abstract {

	public abstract boolean isValid(ClientContext ctx);
	public abstract void perform(ClientContext ctx);
}
