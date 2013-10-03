package se.chalmers.lidkopingsh.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import se.chalmers.lidkopingsh.model.ILayer;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

	// private final HttpResponse httpResponse;
	// private final HttpEntity httpEntity;

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
			// httpResponse = httpClient.execute(httpPost);
			// httpEntity = httpResponse.getEntity();
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
			HttpResponse httpResponse = httpClient.execute(httpPost);
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent()));
		} catch (Exception e) {
			Log.e("server_layer", "Error in HTTP post" + e.toString());
		}
		return reader;
	}

	/**
	 * Gets the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 */
	private void getUpdatedOrdersFromServer(String orderVerifiers) {
		BufferedReader reader = sendHttpPostRequest(orderVerifiers);
		IModel model = ModelHandler.getModel(context);
		ILayer layer = ModelHandler.getLayer(context);
		try {
			Gson gson = new Gson();
			Collection<Order> arrayOrders = gson.fromJson(reader,
					Collection.class);
			for (Order o : arrayOrders) {
				try {
					Order order = model.getOrderById(o.getId());
					order.sync(o);
					layer.updateDatabase(o);
				} catch (NoSuchElementException e) {
					o.addOrderListener(layer);
					model.addOrder(o);
					layer.updateDatabase(o);
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Update database orders.

	}

	@Override
	public void getUpdates() {
		Collection<Order> orders = ModelHandler.getModel(context).getOrders();
		long[][] orderArray = new long[orders.size()][2];
		int i = 0;
		for (Order o : orders) {
			orderArray[i][1] = (long) o.getId();
			orderArray[i][2] = (long) o.getLastTimeUpdate();
			i++;
		}
		Gson gson = new Gson();
		getUpdatedOrdersFromServer(gson.toJson(orderArray));
	}

	@Override
	public void sendUpdate(Order order) {
		Gson gsonOrder = new Gson();
		BufferedReader reader = sendHttpPostRequest(gsonOrder.toJson(order));
		// TODO: check if answer is SUCCESS.
	}

	/**
	 * Update the local database.
	 */
	private void updateDatabase() {
		// TODO Auto-generated method stub

	}

}
