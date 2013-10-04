package se.chalmers.lidkopingsh.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Handling communication between remote server and local database.
 * 
 * @author Alexander H�renstam
 * @author Olliver Mattsson
 * 
 */
public class ServerLayer extends AbstractServerLayer {
	private HttpClient httpClient;
	private HttpPost httpPost;
	private final Context context;

	/**
	 * Creates a new ServerLayer with a set server path.
	 * 
	 * @param serverPath
	 */
	public ServerLayer(String serverPath, Context context) {
		this.serverPath = serverPath;
		this.context = context;
		try {
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(serverPath);
		} catch (Exception e) {
			Log.e("server_layer",
					"Error in HTTP Server Connection" + e.toString());
		}

	}

	/**
	 * Used to send a POST request to server.
	 * 
	 * @param orderString
	 */
	private Response sendHttpPostRequest(String orderString) {
		BufferedReader reader = null;
		Gson gson = new Gson();
		try {
			httpPost.setEntity(new StringEntity(orderString));
			httpPost.setHeader("Lidkopingsh-Authentication",
					"1234567890qwertyuiop");
			HttpResponse httpResponse = httpClient.execute(httpPost);
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent()));

		} catch (Exception e) {
			Log.e("server_layer", "Error in HTTP post " + e.toString());
		}
		return gson.fromJson(reader, Response.class);
	}

	/**
	 * Get the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 */
	private Order[] getUpdatedOrdersFromServer(String orderVerifiers) {
		Response response = sendHttpPostRequest("getUpdates=1&data="
				+ orderVerifiers);

		if (response.isSuccess()) {
			return (Order[]) response.getResults();
		}else{
			printErrorLog(response);
		}
		return null;
	}

	@Override
	public Order[] getUpdates() {
		Collection<Order> orders = ModelHandler.getModel(context).getOrders();
		long[][] orderArray = new long[orders.size()][2];
		int i = 0;
		for (Order o : orders) {
			orderArray[i][1] = (long) o.getId();
			orderArray[i][2] = (long) o.getLastTimeUpdate();
			i++;
		}
		Gson gson = new Gson();
		return getUpdatedOrdersFromServer(gson.toJson(orderArray));
	}

	@Override
	public void sendUpdate(Order order) {
		Gson gsonOrder = new Gson();
		Response response = sendHttpPostRequest(gsonOrder.toJson(order));

		if (!response.isSuccess()) {
			order.sync(null); // Informing that no data has been able to change.
			printErrorLog(response);
		}
	}
	
	private void printErrorLog(Response response) {
		Log.d("server_layer",
				"Error code: "
						+ response.getErrorcode() + " Message: "
						+ response.getMessage());
	}

	private class Response {
		private boolean success;
		private int errorcode;
		private String message;
		private Object[] results;

		public boolean isSuccess() {
			return success;
		}

		public int getErrorcode() {
			return errorcode;
		}

		public String getMessage() {
			return message;
		}

		public Object[] getResults() {
			return results;
		}
	}

}
