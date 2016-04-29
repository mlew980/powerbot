package KaraFisher;

import java.awt.Graphics;

import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt6.ClientContext;

@Script.Manifest(
        name = "[RS3] KaraFisher",
        description = "Lobster/Shark fishing in Kara..",
        properties = "author=darkwalker31;topic=1310017;client=6;version=1.02;"
)

public class KaraFisher extends PollingScript<ClientContext> implements PaintListener {
    
	private State_Abstract[] states;
	
	@Override
    public void start () { 
		final State_Abstract[] temp = {
				new State_Bank(ctx),
				new State_CloseInventoryWindow(ctx),
				new State_Fish(ctx),
				new State_Walk(ctx),
		};
		states = temp;
    }
    @Override
    public void poll() {
    	for (State_Abstract state: states) {
    		if (state.isValid(ctx)) { state.perform(ctx); }
    	}
    }
    
    @Override
    public void repaint(Graphics graphics) {
    	KaraFisherGUI.repaint(this, graphics);
    }
}