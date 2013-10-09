package se.chalmers.lidkopingsh.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
		try {
			httpPost.setEntity(new StringEntity(orderString));
			httpPost.setHeader("LidkopingSH-Authentication",
					"123456789qwertyuiop");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			HttpResponse httpResponse = httpClient.execute(httpPost);
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent()));

		} catch (Exception e) {
			Log.e("server_layer", "Error in HTTP post " + e.toString());
		}
		Response response = null;
		try {
			response  = parseToResponse(reader);
		} catch (Exception e) {
			Log.d("server_layer", "No data from server");
		}
		return response;
	}

	private Response parseToResponse(BufferedReader reader) {
		JsonElement element =  new JsonParser().parse(reader);
		Gson gson = new Gson();
		boolean success = element.getAsJsonObject().get("success").getAsBoolean();
		if(!success) {
			int errorcode = element.getAsJsonObject().get("errorcode").getAsInt();
			String message = element.getAsJsonObject().get("message").getAsString();
			return new Response(success, errorcode, message, null);
		}
		JsonArray json = element.getAsJsonObject().get("results").getAsJsonArray();
		List<Order> orders = new LinkedList<Order>();
		
		for(JsonElement j : json) {
			Order order = gson.fromJson(j, Order.class);
			orders.add(order);
		}
		
		return new Response(success, orders);
		
	}

	/**
	 * Get the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 */
	private Order[] getUpdatedOrdersFromServer(String orderVerifiers) {
		Response response = sendHttpPostRequest("getUpdates=1&data=[[2,1380885442000]]");

		if (response.isSuccess()) {
			Order[] ord = new Order[2];
			int i = 0;
			for(Order o : response.getResults()) {
				ord[i++] = o;
			}
			return ord;
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
			orderArray[i][0] = (long) o.getId();
			orderArray[i][1] = (long) o.getLastTimeUpdate();
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
		private List<Order> results;

		public Response(boolean success, int errorcode, String message, List<Order> results) {
			this(success, results);
			this.errorcode = errorcode;
			this.message = message;
		}
		
		public Response(boolean success, List<Order> results) {
			this.success = success;
			this.results = results;
		}
		
		public boolean isSuccess() {
			return success;
		}

		public int getErrorcode() {
			return errorcode;
		}

		public String getMessage() {
			return message;
		}

		public List<Order> getResults() {
			return results;
		}
	}

}