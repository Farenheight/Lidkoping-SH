package se.chalmers.lidkopingsh.controller;

import se.chalmers.lidkopingsh.app.App;
import se.chalmers.lidkopingsh.server.NetworkStatusListener;
import android.util.Log;

public abstract class NetworkWatcher implements NetworkStatusListener {

	@Override
	public void startedUpdate() {
	}

	@Override
	public void finishedUpdate() {
	}

	@Override
	public void networkProblem(String message) {
		Log.i("NetworkWatcher", "Network error");
		String text = "";
		if (Accessor.isNetworkAvailable()) {
			text = App.getContext().getResources()
					.getString(R.string.network_error_no_server);
		} else {
			text = App.getContext().getResources()
					.getString(R.string.network_error_no_internet);
		}
		RepeatSafeToast.show(text);
	}

	@Override
	public abstract void authenticationFailed();
}
