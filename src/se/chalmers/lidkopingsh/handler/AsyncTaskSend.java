package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.Order;
import android.os.AsyncTask;

public class AsyncTaskSend extends AsyncTask<ServerLayer, Void, Boolean> {
	private Order order;
	
	public AsyncTaskSend(Order order) {
		this.order = order;
	}
	
	@Override
	protected Boolean doInBackground(ServerLayer... serverLayer) {
		return serverLayer[0].sendUpdate(order);
	}

}
