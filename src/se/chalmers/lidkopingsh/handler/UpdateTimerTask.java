package se.chalmers.lidkopingsh.handler;

import java.util.TimerTask;

public class UpdateTimerTask extends TimerTask{

	private OrderDbLayer layer;
	
	public UpdateTimerTask(OrderDbLayer layer) {
		this.layer = layer;
	}
	@Override
	public void run() {
		layer.updateDatabase(layer.getUpdates(false));
	}
	
}
