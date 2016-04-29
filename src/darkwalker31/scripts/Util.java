package KaraFisher;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.Player;

public final class Util {
	private static volatile Util INSTANCE;
	private static final int FISHING = Constants.SKILLS_FISHING;
	
	private ClientContext ctx;
	private final int startLevel;
	private final int startExp;
	
	private Util(ClientContext ctx) {
		this.ctx = ctx;
		this.startLevel = ctx.skills.level(FISHING);
		this.startExp   = ctx.skills.experience(FISHING);
	}
	
	public int getStartLevel() { return startLevel; }
	public int getStartExp()   { return startExp;   }
	public int getCurrentLevel() { return ctx.skills.level(FISHING);      }
	public int getCurrentExp()   { return ctx.skills.experience(FISHING); }
	
	public int getLevelGain() { return ctx.skills.experience(FISHING) - this.startExp; }
	public int getExpGain()   { return ctx.skills.level(FISHING) - this.startLevel;    }
	
	public Player getPlayer() { return ctx.players.local(); }
	
	public int expToNextLevel() {
		int neccExp = ctx.skills.experienceAt(ctx.skills.level(FISHING) + 1);
		return neccExp == -1 ? 0 : neccExp - ctx.skills.experience(FISHING);
	}
	
	public int getProfit() {
		Fish[] list = GlobalVariables.getFishList();
		int profit = 0;
		if (list != null) { 
			for (Fish fish: list) { profit += fish.getValue(); } 
		}
		
		return profit;
	}

	public double distanceToFish (ClientContext ctx) { return distanceTo(ctx, GlobalVariables.FISHING_SPOT); }
	public double distanceToBank (ClientContext ctx) { return distanceTo(ctx, GlobalVariables.STILES_NPC);   }
	public double distanceTo (ClientContext ctx, int NPC) {
		Player local = getPlayer();
		return ctx.npcs.select().id(NPC).nearest().poll().tile().distanceTo(local);
	}
	
	public static Util getInstance(ClientContext ctx) {
		if (INSTANCE == null) {
			synchronized(Util.class) {
				if (INSTANCE == null) { INSTANCE = new Util(ctx); }
			}
		}
		return INSTANCE;
	}
	
	public static Util getInstance() { return INSTANCE; }
}
