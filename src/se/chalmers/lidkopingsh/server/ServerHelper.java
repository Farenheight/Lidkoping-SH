package se.chalmers.lidkopingsh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import se.chalmers.lidkopingsh.handler.Accessor;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Handling communication between remote server and local database.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 * @author Anton Jansson
 * 
 */
class ServerHelper {
	private static final String LIDKOPINGSH_DEVICEID = "Lidkopingsh-Deviceid";
	private static final String LIDKOPINGSH_PASSWORD = "Lidkopingsh-Password";
	private static final String LIDKOPINGSH_USERNAME = "Lidkopingsh-Username";
	private static final String LIDKOPING_SH_DEVICE_ID = "LidkopingSH-DeviceId";
	private static final String LIDKOPINGSH_APIKEY = "Lidkopingsh-Apikey";

	private final String deviceId = "asdf";

	private HttpClient httpClient;
	private final Context context;
	private final String serverPath;
	private SharedPreferences preferences;

	/**
	 * Creates a new ServerHelper with a set server path.
	 * 
	 * @param serverPath
	 */
	public ServerHelper(Context context) {
		preferences = context.getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);

		this.serverPath = preferences.getString(
				ServerSettings.PREFERENCES_SERVER_PATH, null);
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
	 * @throws NetworkErrorException
	 *             if server could not be accessed.
	 */
	private BufferedReader sendHttpPostRequest(String orderString, String action)
			throws NetworkErrorException {
		String apikey = preferences.getString(
				ServerSettings.PREFERENCES_API_KEY, null);
		return sendHttpPostRequest(orderString, action, Arrays.asList(
				new BasicHeader(LIDKOPINGSH_APIKEY, apikey), new BasicHeader(
						LIDKOPING_SH_DEVICE_ID, deviceId)));
	}

	/**
	 * Used to send a POST request to server.
	 * 
	 * @param data
	 * @throws NetworkErrorException
	 *             if server counld not be accessed.
	 */
	private BufferedReader sendHttpPostRequest(String data, String action,
			Collection<? extends Header> headers) throws NetworkErrorException {
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
		} catch (IOException e) {
			throw new NetworkErrorException(e);
		}
		return reader;
	}

	/**
	 * Get the updated orders from the server
	 * 
	 * @param orderVerifiers
	 *            A JsonObject with the ids and timestamps for comparing orders
	 * @throws NetworkErrorException
	 *             if server could not be accessed.
	 * @throws AuthenticationException 
	 */
	private List<Order> getUpdatedOrdersFromServer(String orderVerifiers)
			throws NetworkErrorException, AuthenticationException {
		BufferedReader reader = sendHttpPostRequest("data=" + orderVerifiers,
				"getUpdates");

		ApiResponseGet response = getResponseGet(reader);

		if (isResponseValid(response)) {
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

	/**
	 * Tries to authenticate and retrieve an API key for this device.
	 * 
	 * @param username
	 * @param password
	 * @param deviceId
	 *            Unique device id.
	 * @return Response with success status, error code and message. API key is
	 *         returned in message if it exists.
	 * @throws NetworkErrorException
	 *             if server could not be accessed.
	 * @throws AuthenticationException 
	 */
	public ApiResponse getApikey(String username, String password,
			String deviceId) throws NetworkErrorException, AuthenticationException {
		Collection<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader(LIDKOPINGSH_USERNAME, username));
		headers.add(new BasicHeader(LIDKOPINGSH_PASSWORD, password));
		headers.add(new BasicHeader(LIDKOPINGSH_DEVICEID, deviceId));
		BufferedReader reader = sendHttpPostRequest("", "getApikey", headers);

		ApiResponse response = getResponseSend(reader);
		
		if (isResponseValid(response)) {
			// Store in SharedPreferences
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(ServerSettings.PREFERENCES_API_KEY,
					response.getMessage());
			editor.commit();
		}

		return response;
	}

	/**
	 * Retrieve updates from server.
	 * 
	 * @throws NetworkErrorException
	 *             if server could not be accessed.
	 * @throws AuthenticationException 
	 */
	public List<Order> getUpdates(boolean getAll) throws NetworkErrorException, AuthenticationException {
		Gson gson = new Gson();
		if (getAll) {
			List<Order> allOrders = getUpdatedOrdersFromServer("");
			syncImages(allOrders);
			return allOrders;
		}
		Collection<Order> orders = Accessor.getModel(context).getOrders();
		long[][] orderArray = new long[orders.size()][2];
		int i = 0;
		for (Order o : orders) {
			orderArray[i][0] = (long) o.getId();
			orderArray[i][1] = (long) o.getLastTimeUpdate();
			i++;
		}

		List<Order> newOrders = getUpdatedOrdersFromServer(gson
				.toJson(orderArray));
		syncImages(newOrders);

		return newOrders;
	}

	/**
	 * Send updates to server.
	 * 
	 * @throws NetworkErrorException
	 *             if server could not be accessed.
	 * @throws AuthenticationException 
	 */
	public ApiResponse sendUpdate(Order order) throws NetworkErrorException, AuthenticationException {
		Gson gsonOrder = new Gson();
		String json = "data=" + gsonOrder.toJson(order);
		BufferedReader reader = sendHttpPostRequest(json, "postOrder");

		ApiResponse response = getResponseSend(reader);
		

		if (!isResponseValid(response)) {
			order.sync(null); // Informing that no data has been able to
								// change.
			printErrorLog(response);
		}
		return response;
	}

	private ApiResponseGet getResponseGet(Reader reader) throws JsonSyntaxException, JsonIOException {
		return new Gson().fromJson(reader, ApiResponseGet.class);
	}

	private ApiResponse getResponseSend(Reader reader) throws JsonSyntaxException, JsonIOException {
		return new Gson().fromJson(reader, ApiResponse.class);
	}
	
	private boolean isResponseValid(ApiResponse response) throws AuthenticationException {
		if (response == null) {
			throw new IllegalStateException("Invalid response from server. (response == null)");
		}
		if (!response.isSuccess()) {
			if (response.getErrorcode() == 41) {
				throw new AuthenticationException(response.getMessage());
			}
			return false;
		}
		return true;
	}

	private void printErrorLog(ApiResponse response) {
		Log.d("server_layer", "Error code: " + response.getErrorcode()
				+ " Message: " + response.getMessage());
	}

	public void saveImage(Image i) {
		if (i.getImageFile() == null) {
			URL fileName;
			try {
				// Download file from web server and save it on internal
				// storage.
				fileName = new URL(serverPath + "pics/" + i.getImagePath());
				InputStream is = fileName.openStream();
				OutputStream os = context.openFileOutput(i.getImagePath()
						.replace("/", ""), Context.MODE_PRIVATE);

				byte[] b = new byte[2048];
				int length;

				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}

				is.close();
				os.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void syncImages(List<Order> newOrders) {
		IModel model = Accessor.getModel(context);
		Collection<Order> oldOrders = model.getOrders();
		Collection<Image> oldImages = new LinkedList<Image>();
		Collection<Image> newImages = new LinkedList<Image>();

		for (Order o : newOrders) {
			if (o.isRemoved()) {
				for (Image i : o.getImages()) {
					i.deleteImage();
				}
				newOrders.remove(o);
			}
		}

		// gets all old images
		for (Order oldOrder : oldOrders) {
			oldImages.addAll(oldOrder.getImages());
		}

		// Gets all new images
		for (Order newOrder : newOrders) {
			newImages.addAll(newOrder.getImages());
		}

		// Adds all new images
		addAllNew(newImages, oldImages);

		// Syncs the images that are the same
		syncCommonImages(newImages, oldImages);
	}

	private void addAllNew(Collection<Image> newImages,
			Collection<Image> oldImages) {
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

	private void syncCommonImages(Collection<Image> newImages,
			Collection<Image> oldImages) {
		for (Image newI : newImages) {
			for (Image oldI : oldImages) {
				if (newI.getId() == oldI.getId()
						&& newI.getImagePath() != oldI.getImagePath()) {
					oldI.deleteImage();
					saveImage(newI);
				}
			}
		}
	}

	public class ApiResponseGet extends ApiResponse {
		private List<Order> results;

		public List<Order> getResults() {
			return results;
		}
	}

	public class ApiResponse {
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