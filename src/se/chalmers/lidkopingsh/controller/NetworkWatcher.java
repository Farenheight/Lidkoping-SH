package se.chalmers.lidkopingsh.controller;

import se.chalmers.lidkopingsh.app.App;
import se.chalmers.lidkopingsh.server.NetworkStatusListener;
import se.chalmers.lidkopingsh.server.ServerSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class NetworkWatcher implements NetworkStatusListener {

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
	public void authenticationFailed() {
		logout();
	}

	void logout() {
		App.getContext().startActivity(
				new Intent(App.getContext(), LoginActivity.class));
		Editor editor = App
				.getContext()
				.getSharedPreferences(ServerSettings.PREFERENCES_NAME,
						Context.MODE_PRIVATE).edit();
		editor.clear().commit();
		Accessor.getModel().clearAllOrders();
	}

}
