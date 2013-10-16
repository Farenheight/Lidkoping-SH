package se.chalmers.lidkopingsh.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Handling communication between remote server and local database.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 * @author Anton Jansson
 * 
 */
public class ServerLayer {
	private static final String LIDKOPINGSH_DEVICEID = "Lidkopingsh-Deviceid";
	private static final String LIDKOPINGSH_PASSWORD = "Lidkopingsh-Password";
	private static final String LIDKOPINGSH_USERNAME = "Lidkopingsh-Username";
	private static final String LIDKOPING_SH_DEVICE_ID = "LidkopingSH-DeviceId";
	private static final String LIDKOPINGSH_APIKEY = "Lidkopingsh-Apikey";

	private static final String PREFRENCES_NAME = "AuthenticationPreferences";
	private static final String PREFERENCES_API_KEY = "Apikey";

	private final String deviceId = "asdf";

	private HttpClient httpClient;
	private final Context context;
	private final String serverPath;

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
	private BufferedReader sendHttpPostRequest(String orderString, String action) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFRENCES_NAME, Context.MODE_PRIVATE);
		String apikey = preferences.getString(PREFERENCES_API_KEY, null);
		Log.d("server_layer", "SharedPreferences apikey: " + apikey);

		if (apikey == null) {
			ResponseSend response = getApikey("dev", "dev", deviceId);
			if (response.isSuccess()) {
				Log.d("server_layer", "Get api key success");
				apikey = response.getMessage();
			} else {
				Log.d("server_layer", "Get api key failed");
			}
		}

		return sendHttpPostRequest(orderString, action, Arrays.asList(
				new BasicHeader(LIDKOPINGSH_APIKEY, apikey), new BasicHeader(
						LIDKOPING_SH_DEVICE_ID, deviceId)));
	}

	/**
	 * Used to send a POST request to server.
	 * 
	 * @param data
	 */
	private BufferedReader sendHttpPostRequest(String data, String action,
			Collection<? extends Header> headers) {
		BufferedReader reader = null;
		try {
			HttpPost httpPost = new HttpPost(serverPath + action);
			httpPost.setEntity(new StringEntity(data, HTTP.UTF_8));
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			for (Header h : headers) {
				httpPost.setHeader(h);
			}
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
		BufferedReader reader = sendHttpPostRequest("data=" + orderVerifiers,
				"getUpdates");

		ResponseGet response = getResponseGet(reader);

		if (response != null) {
			if (response.isSuccess()) {
				List<Order> ord = new LinkedList<Order>();
				for (Order o : response.getResults()) {
					ord.add(o);
				}
				return ord;
			} else {
				printErrorLog(response);
			}
		}
		return null;
	}

	/**
	 * Tries to authenticate and retrieve an API key for this device.
	 * 
	 * @param username
	 * @param password
	 * @param deviceId
	 *            Unique device id.
	 * @return Response with success status, error code and message. API key is
	 *         returned in message if it exists.
	 */
	public ResponseSend getApikey(String username, String password,
			String deviceId) {
		Collection<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader(LIDKOPINGSH_USERNAME, username));
		headers.add(new BasicHeader(LIDKOPINGSH_PASSWORD, password));
		headers.add(new BasicHeader(LIDKOPINGSH_DEVICEID, deviceId));
		BufferedReader reader = sendHttpPostRequest("", "getApikey", headers);

		ResponseSend response = getResponseSend(reader);

		// Store in SharedPreferences
		SharedPreferences preferences = context.getSharedPreferences(
				PREFRENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREFERENCES_API_KEY, response.getMessage());
		editor.commit();

		return response;
	}

	/**
	 * Retrieve updates from server.
	 */
	public List<Order> getUpdates(boolean getAll) {
		Gson gson = new Gson();
		if (getAll) {
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
		
		List<Order> newOrders = getUpdatedOrdersFromServer(gson.toJson(orderArray));
		syncImages(newOrders);
		
		return newOrders;
	}

	/**
	 * Send updates to server.
	 */
	public ResponseSend sendUpdate(Order order) {
		Gson gsonOrder = new Gson();
		String json = "data=" + gsonOrder.toJson(order);
		BufferedReader reader = sendHttpPostRequest(json, "postOrder");

		ResponseSend response = getResponseSend(reader);

		if (!response.isSuccess()) {
			if (!response.isSuccess()) {
				order.sync(null); // Informing that no data has been able to
									// change.
				printErrorLog(response);
			}
		}
		return response;
	}

	private ResponseGet getResponseGet(Reader reader) {
		ResponseGet response = null;
		try {
			response = new Gson().fromJson(reader, ResponseGet.class);
		} catch (Exception e) {
			Log.d("server_layer", "No data from server");
		}
		return response;
	}

	private ResponseSend getResponseSend(Reader reader) {
		ResponseSend response = null;
		try {
			response = new Gson().fromJson(reader, ResponseSend.class);
		} catch (Exception e) {
			Log.d("server_layer", "No data from server");
		}
		return response;
	}

	private void printErrorLog(ResponseSend response) {
		Log.d("server_layer", "Error code: " + response.getErrorcode()
				+ " Message: " + response.getMessage());
	}
	
	public void saveImage(Image i) {
		if (i.getImageFile() == null) { 
			try {
				String fileName = Uri.parse(serverPath + "pics/" + i.getImagePath()).getLastPathSegment();
				i.setImageFile(File.createTempFile(fileName, null, context.getCacheDir()));
			} catch(IOException e) {
				Log.e("server_layer", "Error getting Image");
			}
		}
	}

	private void syncImages(List<Order> orders) {
		IModel model = ModelHandler.getModel(context);
		List<Order> newOrders = new LinkedList<Order>();
		newOrders.addAll(orders);
		Collection<Order> oldOrders = model.getOrders();
		
		//removes all images that no longer should exist and adds new ones.
		newOrders.retainAll(oldOrders);
		
		for (Order newOrder : newOrders) {
			Order oldOrder = model.getOrderById(newOrder.getId());
			//copies so that we dont change the actual lists
			Collection<Image> oldImages = new LinkedList<Image>();
			Collection<Image> newImages = new LinkedList<Image>();
			oldImages.addAll(oldOrder.getImages());
			newImages.addAll(newOrder.getImages());
			
			newImages.removeAll(oldImages);
			oldImages.removeAll(newImages);
			
			//removes images that has been removed on the server
			for(Image i : oldImages) {
				i.deleteImage();
			}
			
			//adds new images that we got from server
			for (Image i : newImages) {
				this.saveImage(i);
			}
			
		}
	}
	
	public class ResponseGet extends ResponseSend {
		private List<Order> results;

		public List<Order> getResults() {
			return results;
		}
	}

	public class ResponseSend {
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