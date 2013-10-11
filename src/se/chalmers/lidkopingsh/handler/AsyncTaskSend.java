package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.Order;
import android.os.AsyncTask;

public class AsyncTaskSend extends AsyncTask<SendHelper, Void, Boolean> {

	@Override
	protected Boolean doInBackground(SendHelper... sendHelper) {
		return sendHelper[0].getServerLayer().sendUpdate(sendHelper[0].getOrder());
	}

}
