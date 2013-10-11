package se.chalmers.lidkopingsh.handler;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import se.chalmers.lidkopingsh.model.Order;

public class AsyncTaskGet extends AsyncTask<ServerLayer, Void, List<Order>> {

	@Override
	protected List<Order> doInBackground(ServerLayer... serverLayer) {
		return serverLayer[0].getUpdates();
	}
	
	public void onPostExecute(List<Order> results) {
		Log.e("AsyncTaskGet", results.toString());
	}
}
