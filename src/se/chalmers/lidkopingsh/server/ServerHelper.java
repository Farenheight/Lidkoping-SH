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

import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Handling communication to remote server.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 * @author Anton Jansson
 * 
 */
public class ServerHelper {
	private static final String LIDKOPINGSH_DEVICEID = "Lidkopingsh-Deviceid";
	private static final String LIDKOPINGSH_PASSWORD = "Lidkopingsh-Password";
	private static final String LIDKOPINGSH_USERNAME = "Lidkopingsh-Username";
	private static final String LIDKOPING_SH_DEVICE_ID = "LidkopingSH-DeviceId";
	private static final String LIDKOPINGSH_APIKEY = "Lidkopingsh-Apikey";

	private static final String PICTURES_FOLDER = "pics/";

	private final String deviceId;

	private HttpClient httpClient;
	private final Context context;
	private final String serverPath;
	private SharedPreferences preferences;

	 /**
     * Creates a new ServerHelper for communicating with server. A server path
     * must be set in {@link SharedPreferences}, with name
     * {@link ServerSettings#PREFERENCES_NAME} and key
     * {@link ServerSettings#PREFERENCES_SERVER_PATH}. A unique device id is
     * created for this device.
     * 
     * @param context
     *            The context used for accessing local storage.
     */
	public ServerHelper(Context context) {
		deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
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
     * @param postData
     *            The data to pass in POST body.
     * @param action
     *            The action name of the API method to run.
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
     * @param postData
     *            The data to pass in POST body.
     * @param action
     *            The action name of the API method to run.
     * @throws NetworkErrorException
     *             if server could not be accessed.
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
     * Get the updated orders from the server.
     * 
     * @param orderVerifiers
     *            A JsonObject with the ids and timestamps for comparing orders
     * @throws NetworkErrorException
     *             if server could not be accessed.
     * @throws AuthenticationException
     *             if an authentication problem occurred
     */
	private List<Order> getUpdatedOrdersFromServer(String orderVerifiers)
			throws NetworkErrorException, AuthenticationException {
		BufferedReader reader = sendHttpPostRequest("data=" + orderVerifiers,
				"getUpdates");

		ApiResponseGet response = convertResponseGet(reader);

		if (isResponseValid(response)) {
			List<Order> ord = new LinkedList<Order>();
			for (Order o : response.getResults()) {
				ord.add(o);
			}
			return ord;
		}
		return null;
	}

	/**
     * Tries to authenticate and retrieve an API key for this device.
     * 
     * @param username
     *            Username for web API account.
     * @param password
     *            Password for web API account.
     * @return Response with success status, error code and message. API key is
     *         returned in message if it exists.
     * @throws NetworkErrorException
     *             if server could not be accessed.
     * @throws AuthenticationException
     *             if an authentication problem occurred
     */
	public ApiResponse getApikey(String username, String password)
			throws NetworkErrorException, AuthenticationException {
		Collection<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader(LIDKOPINGSH_USERNAME, username));
		headers.add(new BasicHeader(LIDKOPINGSH_PASSWORD, password));
		headers.add(new BasicHeader(LIDKOPINGSH_DEVICEID, deviceId));
		BufferedReader reader = sendHttpPostRequest("", "getApikey", headers);

		ApiResponse response = convertResponseSend(reader);

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
     * @param getAll
     *            if all orders should be downloaded, or just getting updates.
     * @param currentOrders
     *            if getAll is false, this is the current orders in the model
     *            used for finding which orders that need to be updated.
     * 
     * @return A list of orders which were updated on remote server.
     * 
     * @throws NetworkErrorException
     *             if server could not be accessed.
     * @throws AuthenticationException
     *             if an authentication problem occurred
     */
	public List<Order> getUpdates(boolean getAll,
			Collection<Order> currentOrders) throws NetworkErrorException,
			AuthenticationException {
		Collection<Order> oldOrders = new ArrayList<Order>(currentOrders);
		Gson gson = new Gson();
		if (getAll) {
			List<Order> allOrders = getUpdatedOrdersFromServer("");
			syncImages(allOrders, oldOrders);
			return allOrders;
		}
		long[][] orderArray = new long[oldOrders.size()][2];
		int i = 0;
		for (Order o : oldOrders) {
			orderArray[i][0] = (long) o.getId();
			orderArray[i][1] = (long) o.getLastTimeUpdate();
			i++;
		}

		List<Order> newOrders = getUpdatedOrdersFromServer(gson
				.toJson(orderArray));
		syncImages(newOrders, oldOrders);

		return newOrders;
	}

	/**
     * Send an update to server.
     * 
     * @throws NetworkErrorException
     *             if server could not be accessed.
     * @throws AuthenticationException
     *             if an authentication problem occurred
     */
	public ApiResponse sendUpdate(Order order) throws NetworkErrorException,
			AuthenticationException {
		Gson gsonOrder = new Gson();
		String json = "data=" + gsonOrder.toJson(order);
		BufferedReader reader = sendHttpPostRequest(json, "postOrder");

		ApiResponse response = convertResponseSend(reader);

		if (!isResponseValid(response)) {
			order.sync(null); // Informing that no data has been able to
								// change.
		}
		return response;
	}

	/**
     * Convert a JSON reader response to an ApiResponseGet.
     * 
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
	private ApiResponseGet convertResponseGet(Reader reader)
			throws JsonSyntaxException, JsonIOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Product.class, new ProductDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(reader, ApiResponseGet.class);
	}

	/**
     * Convert a JSON reader response to an ApiResponse.
     * 
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
	private ApiResponse convertResponseSend(Reader reader)
			throws JsonSyntaxException, JsonIOException {
		return new Gson().fromJson(reader, ApiResponse.class);
	}

	/**
     * Validates if the response is correct.
     * 
     * @param response
     *            The response
     * @return Whether the response was valid or not.
     * @throws IllegalStateException
     *             if response is null
     * @throws AuthenticationException
     *             if an authentication problem occurred
     */
	private boolean isResponseValid(ApiResponse response)
			throws AuthenticationException {
		if (response == null) {
			throw new IllegalStateException(
					"Invalid response from server. (response == null)");
		}
		if (!response.isSuccess()) {
			if (response.getErrorcode() >= 40 && response.getErrorcode() < 50) {
				throw new AuthenticationException(response.getMessage());
			}
			Log.d("server_layer", "Error code: " + response.getErrorcode()
					+ " Message: " + response.getMessage());
			return false;
		}
		return true;
	}

	/**
     * Download image from web server and save it to the storage.
     * 
     * @param i
     *            The image to process.
     */
	public void saveImage(Image i) {
		if (i.getImagePath() != null) {
			URL fileName;
			try {
				// Download file from web server and save it on internal
				// storage.
				fileName = new URL(serverPath + PICTURES_FOLDER
						+ i.getServerImagePath());
				InputStream is = fileName.openStream();
				OutputStream os = context.openFileOutput(i.getImagePath(), Context.MODE_PRIVATE);

				byte[] b = new byte[2048];
				int length;

				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}

				is.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	 /**
     * Sync the lists of images, to delete removed orders' images and download
     * new orders' images
     * 
     * @param newOrders
     *            The collection of new orders
     * @param oldOrders
     *            The collection of old orders
     */
	private void syncImages(List<Order> newOrders, Collection<Order> oldOrders) {
		Collection<Image> oldImages = new LinkedList<Image>();
		Collection<Image> newImages = new LinkedList<Image>();
		Collection<Order> newOrderscopy = new LinkedList<Order>(newOrders);
		Collection<Order> removedOrders = new LinkedList<Order>();
		
		for (Order o : newOrderscopy) {
			if (o.isRemoved()) {
				for (Image i : o.getImages()) {
					context.deleteFile(i.getImagePath());
				}
				removedOrders.add(o);
			}
		}

		//removes all deprecated orders
		newOrderscopy.removeAll(removedOrders);
		
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

	/**
     * Save all new images to internal storage.
     * 
     * @param newImages
     *            A collection of new images.
     * @param oldImages
     *            A collection of old images.
     */
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

	/**
     * Redownload images that has a new file name.
     * 
     * @param newImages
     *            A collection of new images.
     * @param oldImages
     *            A collection of old images.
     */
	private void syncCommonImages(Collection<Image> newImages,
			Collection<Image> oldImages) {
		for (Image newI : newImages) {
			for (Image oldI : oldImages) {
				if (newI.getId() == oldI.getId()
						&& !newI.getImagePath().equals(oldI.getImagePath())) {
					context.deleteFile(oldI.getImagePath());
					saveImage(newI);
				}
			}
		}
	}

	/**
     * The response for getting orders that is returned from web API.
     */
	public class ApiResponseGet extends ApiResponse {
		private List<Order> results;

		public List<Order> getResults() {
			return results;
		}
	}

	/**
     * The general response that is returned from web API.
     */
	public class ApiResponse {
		private boolean success;
		private int errorCode;
		private String message;

		public boolean isSuccess() {
			return success;
		}

		public int getErrorcode() {
			return errorCode;
		}

		public String getMessage() {
			return message;
		}
	}
}
