package se.chalmers.lidkopingsh.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
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

	public static final String PREFERENCES_NAME = "AuthenticationPreferences";
	public static final String PREFERENCES_API_KEY = "Apikey";
	public static final String PREFERENCES_SERVER_PATH = "server_path";

	private final String deviceId = "asdf";

	private HttpClient httpClient;
	private final Context context;
	private final String serverPath;
	private SharedPreferences preferences;

	/**
	 * Creates a new ServerLayer with a set server path.
	 * 
	 * @param serverPath
	 */
	public ServerLayer(Context context) {
		preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_PRIVATE);
		
		this.serverPath = preferences.getString(PREFERENCES_SERVER_PATH, null);
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
		String apikey = preferences.getString(PREFERENCES_API_KEY, null);
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
			List<Order> allOrders = getUpdatedOrdersFromServer("");
			syncImages(allOrders);
			return allOrders;
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
			URL fileName;
			try {
				fileName = new URL(serverPath + "pics/" + i.getImagePath());
				InputStream is = fileName.openStream();
				OutputStream os = context.openFileOutput(i.getImagePath(), Context.MODE_PRIVATE);
				byte[] b = new byte[2048];
				int length;
				
				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}
				i.setImageFile(context.openFileInput(i.getImagePath()));
				is.close();
				os.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void syncImages(List<Order> newOrders) {
		IModel model = ModelHandler.getModel(context);
		Collection<Order> oldOrders = model.getOrders();
		Collection<Image> oldImages = new LinkedList<Image>();
		Collection<Image> newImages = new LinkedList<Image>();
		
		for (Order o : newOrders) {
			if (o.isRemoved()) {
				for(Image i : o.getImages()) {
					i.deleteImage();
				}
				newOrders.remove(o);
			}
		}
		
		//gets all old images
		for (Order oldOrder : oldOrders) {
			oldImages.addAll(oldOrder.getImages());
		}
		
		//Gets all new images
		for (Order newOrder : newOrders) {
			newImages.addAll(newOrder.getImages());
		}
		
		//Adds all new images
		addAllNew(newImages, oldImages);
		
		//Syncs the images that are the same
		syncCommonImages(newImages, oldImages);
	}
	
	private void addAllNew(Collection<Image> newImages, Collection<Image> oldImages) {
		Collection<Image> modifiedImages = new LinkedList<Image>();
		modifiedImages.addAll(newImages);
		
		for (Image newI : newImages) {
			for (Image oldI : oldImages) {
				if (newI.getId() == oldI.getId()) {
					modifiedImages.remove(newI);
				}
			}
		}
		for (Image i : modifiedImages) {
			saveImage(i);
		}
	}
	
	private void syncCommonImages(Collection<Image> newImages, Collection<Image> oldImages) {	
		for (Image newI : newImages) {
			for (Image oldI : oldImages) {
				if (newI.getId() == oldI.getId() && newI.getImagePath() != oldI.getImagePath()) {
					oldI.deleteImage();
					saveImage(newI);
				}
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