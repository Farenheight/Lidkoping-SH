package se.chalmers.lidkopingsh.handler;

import java.util.TimerTask;

import android.util.Log;

public class UpdateTimerTask extends TimerTask{

	private OrderDbLayer layer;
	
	public UpdateTimerTask(OrderDbLayer layer) {
		this.layer = layer;
	}
	@Override
	public void run() {
		Log.d("UpdateTimeTask", "run()");
		layer.updateDatabase(layer.getUpdates(false));
	}
	
}
