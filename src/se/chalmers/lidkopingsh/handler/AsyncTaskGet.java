package se.chalmers.lidkopingsh.handler;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import se.chalmers.lidkopingsh.model.Order;

public class AsyncTaskGet extends AsyncTask<ServerLayer, Void, List<Order>> {
	private boolean getAll;
	
	public AsyncTaskGet(boolean getAll) {
		this.getAll = getAll;
	}
	
	@Override
	protected List<Order> doInBackground(ServerLayer... serverLayer) {
		Log.d("AsyncTaskGet", "doInBackground");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return serverLayer[0].getUpdates(getAll);
	}
	
	public void onPostExecute(List<Order> results) {
		Log.d("AsyncTaskGet", results.toString());
	}
}
