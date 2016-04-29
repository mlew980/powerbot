package KaraFisher;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.TilePath;

public class State_Walk extends State_Abstract {
	
	private final TilePath pathToFish;
	private final TilePath pathToBank;
	
	public State_Walk(ClientContext ctx) {
		pathToFish = new TilePath(ctx, GlobalVariables.PATH);
        pathToBank = pathToFish.reverse();
	}
	
	@Override
	public boolean isValid(ClientContext ctx) {
		Util util = Util.getInstance(ctx);
		return util.distanceToFish(ctx) >= 15 && util.distanceToBank(ctx) >= 15;
	}

	@Override
	public void perform(ClientContext ctx) {
		KaraFisherGUI.setStatus("Walking");
		TilePath path = ctx.backpack.select().count() < 28 ? pathToFish : pathToBank;
		if (path.valid()) { path.traverse(); }
	}

}
