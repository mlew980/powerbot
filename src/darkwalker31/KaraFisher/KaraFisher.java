package KaraFisher;

import java.awt.Graphics;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.TilePath;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

@Script.Manifest(
        name = "KaraFisher",
        description = "Lobster/Shark fishing in Kara..",
        properties = "author=darkwalker31;topic=1310317;client=6;version=1.02;"
)

public class KaraFisher extends PollingScript<ClientContext> implements PaintListener {

	//Declare current variables, these will be used in GUI
	public int currLevel = 0;
	public int startLevel = ctx.skills.level(Constants.SKILLS_FISHING);
	public int currentExp = 0;
	public int startExp = ctx.skills.experience(Constants.SKILLS_FISHING);
	public int amountLobster = 0;
	public int amountTuna = 0;
	public int amountSwordFish = 0;
	public int profit = 0;
	public String status = "";
    
	//ID constants
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

    //Get the current price for lobster on GE
    public static final int priceLobster = new GeItem(LOBSTER).price;
    public static final int priceTuna = new GeItem(SWORD_FISH).price;
    public static final int priceSwordFish = new GeItem(TUNA).price;
    
    TilePath pathToTrader, pathToFishingSpot;


    @Override
    public void start () {
    	pathToTrader = new TilePath(ctx, PATH);
        pathToFishingSpot = new TilePath(ctx, PATH).reverse();
    }
    @Override
    public void poll() {
    	
    	//Check current state
        final State state = getState();
        if (state == null) {
            return;
        }
        
        //Main logic for each state
        switch (state) {
        	
            case WALK_TO_FISHING_SPOT:
            	//Set GUI status
            	status = "Walking to fishing spot.";
            	//Traverse to the fishing spot from trader.
        		if (pathToFishingSpot.valid()) {
        			pathToFishingSpot.traverse();
            	}
                break;
            //Cage Lobsters or Harpoon Sharks based on level
            case FISH:
            	//Set GUI status
            	status = "Fishing.";
                
            	//Get the closest fishing spot
            	final Npc fishing_Spot = ctx.npcs.select().id(FISHING_SPOT).nearest().poll();
            	ctx.camera.turnTo(fishing_Spot);
            	//If fishing level is over 50 then Harpoon, otherwise Cage
            	if(currLevel >= 50){
            		//Harpoon the fishing spot
            		if(fishing_Spot.id() == FISHING_SPOT && fishing_Spot.interact("Harpoon")){
	            		//Check if still fishing and if bag is full
	                	while (ctx.players.local().interacting().valid() & ctx.backpack.select().count() != 28) {
	                		Condition.sleep(Random.nextInt(800, 1000));
	                		amountTuna = ctx.backpack.select().id(TUNA_NOTES).count(true) + ctx.backpack.select().id(TUNA).count();
	                        amountSwordFish = ctx.backpack.select().id(SWORD_FISH_NOTES).count(true) + ctx.backpack.select().id(SWORD_FISH).count();
	                		if(ctx.widgets.widget(FULL_INV_WINDOW).valid() & ctx.backpack.select().count() != 28){
	                			ctx.widgets.component(FULL_INV_WINDOW,FULL_INV_COMPONENT).click();
	                			//Set GUI status
	                			status = "Finishd Fishing.";
	                			break;
	                		}
	                	}
            		}
            	} else if (currLevel >= 40 & currLevel < 50){
            		if(fishing_Spot.id() == FISHING_SPOT && fishing_Spot.interact("Cage")){
            			while (ctx.players.local().interacting().valid()) {
                    		Condition.sleep(Random.nextInt(800, 1000));
                    		amountLobster = ctx.backpack.select().id(LOBSTER_NOTES).count(true) + ctx.backpack.select().id(LOBSTER).count();
                    		if(ctx.widgets.widget(FULL_INV_WINDOW).valid()){
                    			ctx.widgets.component(FULL_INV_WINDOW,FULL_INV_COMPONENT).click();
                    			//Set GUI status
                    			status = "Finishd Fishing.";
                    			break;
                    		}
                    	}
            		}
            	} else {
            		status = "Fishing too low.";
            		ctx.controller.suspend();
            	}
            	
                break;
            case WALK_TO_TRADE_FISH:
            	//Set GUI status
            	status = "Walking to trading spot.";
            	//Traverse to the fishing spot from trader.
        		if (pathToTrader.valid()) {
        			pathToTrader.traverse();
            	} 
                break;
            case TRADE_FISH:
            	//Set GUI status
            	status = "Trading.";
            	final Npc trader = ctx.npcs.select().id(STILES_NPC).nearest().poll();
            	ctx.camera.turnTo(trader);
                trader.interact("Exchange");
                break;
            case IDLE:
            	status = "Idle.";
            	break;
            default:
            	status = "Idle.";
            	break;   
        }
    }

    //Get current state based on specific information
    private State getState() {
    	/*
    	 * 	Condition 1 - Check if :
    	 * 		1. Fishing spot is within view
    	 * 		2. Backpack isin't full
    	 * 		3. Fishing spot is within 30 tile radius.
    	 * 	Condition 2 - Check if :
    	 * 		1. Trader is within view
    	 * 		2. Have some lobsters in the bag
    	 *		3. Trader is within 10 tiles
    	 *	Condition 3 - Check if :
    	 *		1. Backpack is full
    	 *	Condition 4 - Check if :
    	 *		1. No lobsters (hard not note) are present within backpack.
    	 *		2. Fishing spot isin't within view
    	 *		3. Fishing spot is over 20 tiles away (inclusive)
    	 */
    	if (ctx.backpack.select().count() != 28 & (ctx.npcs.select().id(FISHING_SPOT).nearest().poll().tile().distanceTo(ctx.players.local()) < 15)) {
            return State.FISH;
	    } else if(ctx.npcs.select().id(STILES_NPC).nearest().poll().inViewport() & (ctx.npcs.select().id(STILES_NPC).nearest().poll().tile().distanceTo(ctx.players.local()) < 10) & !ctx.backpack.select().id(TUNA, LOBSTER, SWORD_FISH).isEmpty()){
	    	return State.TRADE_FISH;
	    } else if (ctx.backpack.select().count() == 28) {
	        return State.WALK_TO_TRADE_FISH; 
	    } else if (ctx.backpack.select().id(LOBSTER).count() == 0 & ctx.backpack.select().id(TUNA).count() == 0 & ctx.backpack.select().id(SWORD_FISH).count() == 0 & (ctx.npcs.select().id(FISHING_SPOT).nearest().poll().tile().distanceTo(ctx.players.local()) >= 15)) {
	        return State.WALK_TO_FISHING_SPOT;
	    } else {
	    	return State.IDLE;
	    }
    }
    
    //List of different program states
    private enum State {
    	WALK_TO_FISHING_SPOT, FISH, WALK_TO_TRADE_FISH, TRADE_FISH, IDLE
    }
    
    //Set the font to Tahoma, for purposes.
    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    //Convert long type millisecond timer into a String showing hh:mm:ss, helper class.
    public static String Time(long i) {
    	DecimalFormat nf = new DecimalFormat("00");
    	long millis = i;
    	long hours = millis / (1000 * 60 * 60);
    	millis -= hours * (1000 * 60 * 60);
    	long minutes = millis / (1000 * 60);
    	millis -= minutes * (1000 * 60);
    	long seconds = millis / 1000;
    	return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
	}
    
    //Method which gets time till next level.
    public String getTimeToNextLevel(final int expLeft, final int xpPerHour) {
    	
    	//If not earning exp then return
	    if (xpPerHour < 1) {
		    return "No EXP gained yet.";
	    }
	    
	    //If gaining exp then measure approximately it will take to level.
	    return Time((long)(expLeft * 3600000D / xpPerHour));
    }
    
    //GUI Paint Class
    @Override
    public void repaint(Graphics graphics) {
    	//Create graphics object
        final Graphics2D g = (Graphics2D) graphics;
        
        //Set default font to Tahoma
        g.setFont(TAHOMA);
        
        //Set color to Black
        g.setColor(Color.BLACK);
        
        //Fill a rectangle using the color above
        g.fillRect(5, 5, 205, 245);

        /* 	CALCULATING PROFIT
         * 	amount<X> - Gives the amount of <X> currently present both in hard form and note form
         * 	profit - Gives the current price on GE for lobster, tuna and swordfish
         */
        
        profit = (priceLobster * amountLobster) + (priceTuna * amountTuna) + (priceSwordFish * amountSwordFish);
      
        //Current EXP in Fishing
        currentExp = ctx.skills.experience(Constants.SKILLS_FISHING);
        currLevel = ctx.skills.level(Constants.SKILLS_FISHING);
        
        //Levels gained since starting script
        int levelGain = currLevel - startLevel;
        
        //EXP gained since starting script
        int expGain = currentExp - startExp;
        
        //EXP left before level
        int expLeft = ctx.skills.experienceAt(currLevel + 1) - currentExp;
        
        //Amount of time in milliseconds that the script has been running
        long runTime = getRuntime();
        
        //Amount of EXP gained per hour
        int expPh = (int) (3600000d / (long) runTime * (double) (expGain));
        
        //Amount of time till next level in hh:mm:ss format
        String timeLeft = getTimeToNextLevel(expLeft,expPh);
        
        //Set color to white (for text)
        g.setColor(Color.WHITE);
        
        //Draw text onto boxes
        g.drawString(String.format("KaraFisher"), 10, 20);
        g.drawString(String.format("Fishing Level: %d", currLevel), 10, 40);
        g.drawString(String.format("Levels Gained: %d", levelGain), 10, 60);
        g.drawString(String.format("EXP Gained: %d", expGain), 10, 80);
        g.drawString(String.format("EXP Left: %d", expLeft), 10, 100);
        g.drawString(String.format("EXP p/h: %d", expPh), 10, 120);
        g.drawString(String.format("Time to Level: %s", timeLeft), 10, 140);
        g.drawString(String.format("Lobsters: %d", amountLobster), 10, 160);
        g.drawString(String.format("Tuna: %d", amountTuna), 10, 180);
        g.drawString(String.format("Swordfish: %d", amountSwordFish), 10, 200);
        g.drawString(String.format("Profit: %d", profit), 10, 220);
        g.drawString(String.format("Status: %s", status), 10, 240);
    }
}