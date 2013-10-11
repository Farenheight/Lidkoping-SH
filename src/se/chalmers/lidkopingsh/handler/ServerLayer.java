package se.chalmers.lidkopingsh.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

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
	private static boolean first = true;

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
	private BufferedReader sendHttpPostRequest(String orderString) {
		BufferedReader reader = null;
		try {
			httpPost.setEntity(new StringEntity(orderString));
			httpPost.setHeader("LidkopingSH-Authentication",
					"123456789qwertyuiop");
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			HttpResponse httpResponse = httpClient.execute(httpPost);
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent()));

		} catch (Exception e) {
			Log.e("server_layer", "Error in HTTP post " + e.toString());
		}
		return reader;
	}

	/**
	 * Get the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 */
	private List<Order> getUpdatedOrdersFromServer(String orderVerifiers) {
		BufferedReader reader = sendHttpPostRequest("getUpdates=1&data="
				+ orderVerifiers);

		ResponseGet response = null;
		try {
			response = new Gson().fromJson(reader, ResponseGet.class);
		} catch (Exception e) {
			Log.d("server_layer", "No data from server");
		}
		
		if (response.isSuccess()) {
			List<Order> ord = new LinkedList<Order>();
			for (Order o : response.getResults()) {
				ord.add(o);
			}
			return ord;
		} else {
			printErrorLog(response);
		}
		return null;
	}

	@Override
	public List<Order> getUpdates() {
		Gson gson = new Gson();
		if (first) {
			first = false;
			return getUpdatedOrdersFromServer("");
		}
		Collection<Order> orders = ModelHandler.getModel(context).getOrders();
		long[][] orderArray = new long[orders.size()][2];
		int i = 0;
		for (Order o : orders) {
			orderArray[i][0] = (long) o.getId();
			orderArray[i][1] = (long) o.getLastTimeUpdate();
			i++;
		}
		return getUpdatedOrdersFromServer(gson.toJson(orderArray));
	}

	@Override
	public boolean sendUpdate(Order order) {
		Gson gsonOrder = new Gson();
		BufferedReader reader = sendHttpPostRequest("postOrder=1&data=" + gsonOrder.toJson(order));
		String line = "";
		try {
			line = reader.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ResponseSend response = null;
		try {
			response = new Gson().fromJson(reader, ResponseSend.class);
		} catch (Exception e) {
			Log.d("server_layer", "No data from server");
		}

		if (!response.isSuccess()) {
			order.sync(null); // Informing that no data has been able to change.
			printErrorLog(response);
			return response.isSuccess();
		}
		return response.isSuccess();
	}

	private void printErrorLog(ResponseSend response) {
		Log.d("server_layer", "Error code: " + response.getErrorcode()
				+ " Message: " + response.getMessage());
	}

	private class ResponseGet extends ResponseSend {
		private List<Order> results;
		
		public List<Order> getResults() {
			return results;
		}
	}
	
	private class ResponseSend {
		private boolean success;
		private int errorcode;
		private String message;
		
		public boolean isSuccess() {
			return success;
		}

		public int getErrorcode() {
			return errorcode;
		}

		public String getMessage() {
			return message;
		}
	}

}