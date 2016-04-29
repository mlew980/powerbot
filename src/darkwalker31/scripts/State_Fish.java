package KaraFisher;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Npc;

public class State_Fish extends State_Abstract {

	public State_Fish(ClientContext ctx) { }
	
	@Override
	public boolean isValid(ClientContext ctx) {
		Util util = Util.getInstance(ctx);
		return util.distanceToFish(ctx) < 15 && ctx.backpack.select().count() < 28 && util.getPlayer().idle();
	}

	@Override
	public void perform(ClientContext ctx) {
		Util util = Util.getInstance(ctx);
		int level;
		
		if ((level = util.getCurrentLevel()) < 40) { 
			KaraFisherGUI.setStatus("Fishing level too low!");
			ctx.controller.suspend();
			return;
		}
		
		KaraFisherGUI.setStatus("Fishing");

		String interaction = level >= 50 ? "Harpoon" : "Cage";
		if (util.getPlayer().idle()) {
			final Npc fishingSpot = ctx.npcs.select().id(GlobalVariables.FISHING_SPOT).nearest().poll();
			ctx.camera.turnTo(fishingSpot);
			fishingSpot.interact(interaction);
		}
	}

}
