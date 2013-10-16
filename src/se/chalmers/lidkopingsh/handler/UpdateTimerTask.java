package se.chalmers.lidkopingsh.handler;

import java.util.TimerTask;

import android.os.Handler;

public class UpdateTimerTask extends TimerTask{

	private OrderDbLayer layer;
	private Handler handler;
	
	public UpdateTimerTask(OrderDbLayer layer) {
		this.layer = layer;
		handler = new Handler();
	}

	
	@Override
	public void run() {
		handler.post(new Runnable() {
	        public void run() {       
	    		layer.update(false);
	        }
	    });
	}
	
}
