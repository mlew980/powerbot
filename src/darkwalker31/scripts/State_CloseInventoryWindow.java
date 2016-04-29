package KaraFisher;

import org.powerbot.script.rt6.ClientContext;

public class State_CloseInventoryWindow extends State_Abstract {

	public State_CloseInventoryWindow(ClientContext ctx) {}
	
	@Override
	public boolean isValid(ClientContext ctx) {
		return ctx.widgets.widget(GlobalVariables.FULL_INV_WINDOW).valid();
	}
	
	@Override
	public void perform(ClientContext ctx) {
		KaraFisherGUI.setStatus("Finished Fishing");
		ctx.widgets.component(GlobalVariables.FULL_INV_WINDOW, GlobalVariables.FULL_INV_COMPONENT).click();
	}

}
