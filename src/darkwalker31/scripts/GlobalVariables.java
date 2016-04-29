package KaraFisher;

import org.powerbot.script.Tile;

public class GlobalVariables {
	
	//ID Constants
	public static final int STILES_NPC = 11267;
    public static final int FISHING_SPOT = 324;
    public static final int LOBSTER = 377;
    public static final int LOBSTER_NOTES = 378;
    public static final int SWORD_FISH = 371;
    public static final int SWORD_FISH_NOTES = 372;
    public static final int TUNA = 359;
    public static final int TUNA_NOTES = 360;
    
    //Widget Constants
    public static final int FULL_INV_WINDOW = 1186;
    public static final int FULL_INV_COMPONENT = 4;

    //Fixed Path that goes from fishing spot to trader
    public static final Tile[] PATH = {
            new Tile(2924, 3173, 0),
            new Tile(2918, 3158, 0),
            new Tile(2910, 3152, 0),
            new Tile(2892, 3149, 0),
            new Tile(2874, 3151, 0),
            new Tile(2858, 3148, 0),
            new Tile(2852, 3143, 0)
    };
	

	private static volatile Fish[] fishList;
	
	public static Fish[] getFishList() { return getFishList(TUNA, SWORD_FISH, LOBSTER); }
	public static Fish[] getFishList(int... ids) {
		if (fishList == null) {
			synchronized (GlobalVariables.class) {
				if (fishList == null) {
					fishList = new Fish[ids.length];
					int index = 0;
					for (int id: ids) { fishList[index++] = new Fish(id); }
				}
			}
		}
		return fishList;
	}
	
}
