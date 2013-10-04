package se.chalmers.lidkopingsh.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import se.chalmers.lidkopingsh.model.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
	private BufferedReader sendHttpPostRequest(String orderString) {
		BufferedReader reader = null;
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
		return reader;
	}

	/**
	 * Get the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 */
	private Collection<Order> getUpdatedOrdersFromServer(String orderVerifiers) {
		BufferedReader reader = sendHttpPostRequest("getUpdates=1&data="
				+ orderVerifiers);
		try {
			Gson gson = new Gson();
			return gson.fromJson(reader,
					new TypeToken<Collection<Order>>() {
					}.getType());
		} catch (JsonSyntaxException e) {
			Log.e("server_layer", "Error in DB Update " + e.toString());
		}
		return null;
	}

	@Override
	public Collection<Order> getUpdates() {
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
		BufferedReader reader = sendHttpPostRequest(gsonOrder.toJson(order));
		// TODO: check if answer is SUCCESS.
	}

}
