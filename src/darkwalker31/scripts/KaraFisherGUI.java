package KaraFisher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.powerbot.script.AbstractScript;
import org.powerbot.script.rt6.ClientContext;

public class KaraFisherGUI {
	
    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
    public static String status = "Starting up...";
    
    public static void setStatus(String str) { status = str; }
	
	public static void repaint(AbstractScript<ClientContext> script, Graphics graphics) {
		final Util util = Util.getInstance();
		if (util == null) { return; }
		
		final Graphics2D g = (Graphics2D) graphics;

		//Set default font to Tahoma
		g.setFont(TAHOMA);

		//Set color to Black
		g.setColor(Color.BLACK);

		//Fill a rectangle using the color above
		g.fillRect(5, 5, 205, 245);
		long runTime = script.getRuntime();
		int expPh = (int) (3600000d / runTime * util.getExpGain()); //EXP gain/hour
		int tNextLevel = (int) (util.expToNextLevel() / (expPh / 60D));

		//Set color to white (for text)
		g.setColor(Color.WHITE);

		//Draw text onto boxes
		g.drawString(String.format("[RS3] KaraFisher"), 10, 20);
		g.drawString(String.format("Fishing Level: %d", util.getCurrentLevel()), 10, 40);
		g.drawString(String.format("Levels Gained: %d", util.getLevelGain()), 10, 60);
		g.drawString(String.format("EXP Gained: %d", util.getExpGain()), 10, 80);
		g.drawString(String.format("EXP Left: %d", util.expToNextLevel()), 10, 100);
		g.drawString(String.format("EXP p/h: %d", expPh), 10, 120);
		g.drawString(String.format("Time to Level: %s", tNextLevel), 10, 140);
		g.drawString(String.format("Profit: %d", util.getProfit()), 10, 160);
		g.drawString(String.format("Status: %s", status), 10, 180);
		
		int index = 0;
		for (Fish fish: GlobalVariables.getFishList()) {
			g.drawString(String.format("%s : %d", fish.getName(), fish.getCount()), 10, 200 + 20*index);
		}
	}
}
