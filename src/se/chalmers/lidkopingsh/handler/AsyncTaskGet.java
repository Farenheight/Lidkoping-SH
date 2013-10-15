package se.chalmers.lidkopingsh.handler;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import se.chalmers.lidkopingsh.model.Order;

public class AsyncTaskGet extends AsyncTask<ServerLayer, Void, List<Order>> {
	private final boolean getAll;
	private final OrderDbLayer layer;
	
	public AsyncTaskGet(boolean getAll, OrderDbLayer layer) {
		this.getAll = getAll;
		this.layer = layer;
	}
	
	@Override
	protected List<Order> doInBackground(ServerLayer... serverLayer) {
		return serverLayer[0].getUpdates(getAll);
	}
	
	public void onPostExecute(List<Order> orders) {
		layer.updateDatabase(orders);
	}
}
