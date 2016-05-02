package KebabBuyer;

import java.awt.Graphics;

import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ChatOption;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.TilePath;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "[RS3] KebabBuyer",
        description = "Buys kebabs from Karim in Al Kharid.",
        properties = "author=darkwalker31;topic=1310209;client = 6;version=1.0"
)

public class KebabBuyer extends PollingScript<ClientContext> implements MessageListener, PaintListener {

	//Declare current variables, these will be used in GUI
	public int amountKebab = 0;
	public int goldCurrent = 0;
	public int goldStart = 0;
	public int profit = 0;
	public String status = "";
    
	//Declare constants
    public static final int KEBAB = 1971;
    public static final int KARIM = 543;
    public static final int GOLD = 995;
    public static final String CHAT_OPTIONS = "Yes please.";

    public static final Tile[] PATH = {
            new Tile(3275, 3167, 0),
            new Tile(3276, 3177, 0),
            new Tile(3277, 3182, 0),
            new Tile(3271, 3182, 0)
    };

    public static final int priceKebab = new GeItem(KEBAB).price - 1;
    
    TilePath pathToTrader, pathToBank;


    @Override
    public void start () {
    	pathToTrader = new TilePath(ctx, PATH);
    	pathToBank = new TilePath(ctx, PATH).reverse();
    	goldStart = ctx.backpack.moneyPouchCount();
    }
    @Override
    public void poll() {
    	
    	//Check current state
        final State state = getState();
        if (state == null) {
            return;
        }
        
        switch (state) {
        	
            case WALK_TO_TRADER:
            	//Set GUI status
            	status = "Walking to trader.";
        		if (pathToTrader.valid()) {
        			pathToTrader.traverse();
            	}
                break;
            case BUY:
            	//Set GUI status
            	status = "Buying kebabs.";
            	
                if (!ctx.chat.chatting()) {
                    final Npc trader = ctx.npcs.select().id(KARIM).nearest().poll();
                    if (trader.interact("Talk-to")) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.chat.chatting();
                            }
                        }, 250, 10);
                    }
                } else if (ctx.chat.canContinue()) {
                    ctx.chat.clickContinue(true);
                    Condition.sleep(Random.nextInt(350, 500));
                } else if (!ctx.chat.select().text(CHAT_OPTIONS).isEmpty()) {
                    final ChatOption option = ctx.chat.poll();
                    if (option.select(true)) {
                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return !option.valid();
                            }
                        }, 250, 10);
                        
                    }
                }
                break;
            case WALK_TO_BANK:
            	//Set GUI status
            	status = "Walking to bank.";
        		if (pathToBank.valid()) {
        			pathToBank.traverse();
            	} 
                break;
            case BANK:
            	//Set GUI status
            	status = "Banking kebabs.";
                if (!ctx.bank.opened()) {
                    ctx.bank.open();
                } else if (goldCurrent == 0) {
                	if(ctx.bank.select().id(GOLD).count()> 0){
                		ctx.bank.withdraw(GOLD, ctx.bank.select().id(GOLD).count());
                	} else {
                		ctx.controller.stop();
                	}
        		} else if (!ctx.backpack.select().id(KEBAB).isEmpty()) {
                    ctx.bank.depositInventory();
                } else {
                    ctx.bank.close();
                }
                break;
               
        }
    }

    //Get current state based on specific information
    private State getState() {
    	if (ctx.bank.opened()) {
            return State.BANK;
    	} else if (goldCurrent == 0){
    		return State.WALK_TO_BANK;
        } else if (ctx.backpack.select().count() < 28) {
            if (!ctx.npcs.select().id(KARIM).within(10).isEmpty()) {
                return State.BUY;
            } else {
                return State.WALK_TO_TRADER;
            }
        } else if (!ctx.bank.inViewport()) {
            return State.WALK_TO_BANK;
        } else if (ctx.bank.nearest().tile().distanceTo(ctx.players.local()) < 10) {
            return State.BANK;
        }
        return null;
    }
    
    //List of different program states
    private enum State {
    	WALK_TO_BANK, BANK, WALK_TO_TRADER, BUY
    }
    
    //Set the font to Tahoma, for purposes.
    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    //Convert long type millisecond timer into a String showing hh:mm:ss, helper class.
    public String Time(long i) {
    	DecimalFormat nf = new DecimalFormat("00");
    	long millis = i;
    	long hours = millis / (1000 * 60 * 60);
    	millis -= hours * (1000 * 60 * 60);
    	long minutes = millis / (1000 * 60);
    	millis -= minutes * (1000 * 60);
    	long seconds = millis / 1000;
    	return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
	}

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.equals("you buy a kebab.")) {
        	amountKebab += 1;
        }
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
        g.fillRect(5, 5, 205, 105);

        profit = (priceKebab * amountKebab);
        
        goldCurrent = ctx.backpack.moneyPouchCount();
        
        //Amount of time in milliseconds that the script has been running
        long runTime = getRuntime();
       
        
        //Set color to white (for text)
        g.setColor(Color.WHITE);
        
        //Draw text onto boxes
        g.drawString(String.format("[RS3] KebabBuyer v1.00"), 10, 20);
        g.drawString(String.format("Kebabs: %d", amountKebab), 10, 40);
        g.drawString(String.format("Gold: %d", goldCurrent), 10, 60);
        g.drawString(String.format("Profit: %d", profit), 10, 80);
        g.drawString(String.format("Run Time: %s", Time(runTime)), 10, 100);
    }
}