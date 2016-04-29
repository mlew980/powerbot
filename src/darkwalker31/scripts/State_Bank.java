package KaraFisher;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.TilePath;

public class State_Bank extends State_Abstract {
	
	private final TilePath path;
	
	public State_Bank(ClientContext ctx) {
		path = new TilePath(ctx, GlobalVariables.PATH).reverse();
	}
	
	@Override
	public boolean isValid(ClientContext ctx) {
		Util util = Util.getInstance(ctx);
		return util.distanceToBank(ctx) < 15;
	}

	@Override
	public void perform(ClientContext ctx) {
		if (ctx.backpack.select().count() == 28) {
			KaraFisherGUI.setStatus("Trading");
			final Npc trader = ctx.npcs.select().id(GlobalVariables.STILES_NPC).nearest().poll();
        	ctx.camera.turnTo(trader);
            trader.interact("Exchange");
		} else {
			KaraFisherGUI.setStatus("Walking");
			if (path.valid()) path.traverse();
		}
	}

}
