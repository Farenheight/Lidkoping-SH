package se.chalmers.lidkopingsh.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.message.BasicHeader;

import se.chalmers.lidkopingsh.app.App;
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
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * Handling communication to remote server.
 * <p>
 * The server path that is used must be set in a {@link SharedPreferences} named
 * {@link ServerSettings#PREFERENCES_NAME}. Set the value for key
 * {@link ServerSettings#PREFERENCES_SERVER_PATH} as URL, including
 * <i>http://</i> or appropriate.
 * <p>
 * Accessing data from server requires an API key. Therefore, before requesting
 * other methods, {@link #getApikey(String, String)} must be called to
 * authenticate current user. When {@link #getApikey(String, String)} has been
 * called with a successful response, the API key for this user and device is
 * stored in {@link SharedPreferences} named
 * {@link ServerSettings#PREFERENCES_NAME} containing a value for the key
 * {@link ServerSettings#PREFERENCES_API_KEY}.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 * @author Anton Jansson
 * 
 */
public class ServerHelper {
	/** Certificate file located in /assets/ folder. */
	private static final String CERTIFICATE_FILENAME = "certificate.pem";

	private static final String LIDKOPINGSH_DEVICEID = "Lidkopingsh-Deviceid";
	private static final String LIDKOPINGSH_PASSWORD = "Lidkopingsh-Password";
	private static final String LIDKOPINGSH_USERNAME = "Lidkopingsh-Username";
	private static final String LIDKOPING_SH_DEVICE_ID = "LidkopingSH-DeviceId";
	private static final String LIDKOPINGSH_APIKEY = "Lidkopingsh-Apikey";

	private static final String PICTURES_FOLDER = "pics/";

	private final String deviceId;

	private final SSLContext sslContext;
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
	public ServerHelper() {
		deviceId = Secure.getString(App.getContext().getContentResolver(),
				Secure.ANDROID_ID);
		preferences = App.getContext().getSharedPreferences(
				ServerSettings.PREFERENCES_NAME, Context.MODE_PRIVATE);

		this.serverPath = preferences.getString(
				ServerSettings.PREFERENCES_SERVER_PATH, null);

		// Initiate certificate and set SSL context.
		sslContext = createSSLContext();
	}

	/**
	 * Loads the certificate to trust the self-signed certificate on web server.
	 * This is stored in SSL context, which must be used by the
	 * {@link HttpsURLConnection}.
	 * 
	 * @return An SSL context to use in a {@link HttpsURLConnection}.
	 */
	private SSLContext createSSLContext() {
		InputStream certInput = null;
		try {
			certInput = App.getContext().getAssets().open(CERTIFICATE_FILENAME);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		SSLContext context = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate ca = cf.generateCertificate(certInput);
			Log.d("ServerHelper", "Loading certificate="
					+ ((X509Certificate) ca).getSubjectDN());

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			return context;
		} catch (IOException e) {
			throw new IllegalStateException(
					"Error while loading certificate from file.", e);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(
					"Error while loading certificate from file.", e);
		} finally {
			try {
				certInput.close();
			} catch (IOException e) {
			}
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
	private String sendHttpPostRequest(String orderString, String action)
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
	private String sendHttpPostRequest(String data, String action,
			Collection<? extends Header> headers) throws NetworkErrorException {
		BufferedReader reader = null;
		try {
			URL url = new URL(serverPath + action);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url
					.openConnection();

			urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			// Headers
			urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			for (Header h : headers) {
				urlConnection.setRequestProperty(h.getName(), h.getValue());
			}

			// Send request
			DataOutputStream wr = new DataOutputStream(
					urlConnection.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();

			// Get response
			InputStream in = urlConnection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkErrorException(e);
		}
		return convertStreamToString(reader);
	}
	


    /**
     * Get reader content as a string.
     * 
     * @param reader
     *            The reader.
     * @return The content of the reader.
     */
    private String convertStreamToString(Reader reader) {
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
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
		String json = sendHttpPostRequest("data=" + orderVerifiers,
				"getUpdates");

		ApiResponseGet response = convertResponseGet(json);

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
		String json = sendHttpPostRequest("", "getApikey", headers);

		ApiResponse response = convertResponseSend(json);
		Log.d("ServerHelper", "Response from getApiKey: " + response.message);

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
		String jsonInput = "";
		try {
			jsonInput = "data="
					+ URLEncoder.encode(gsonOrder.toJson(order), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String jsonResponse = sendHttpPostRequest(jsonInput, "postOrder");

		ApiResponse response = convertResponseSend(jsonResponse);

		if (!isResponseValid(response)) {
			order.sync(null); // Informing that no data has been able to
								// change.
		}
		return response;
	}

	/**
	 * Convert a JSON reader response to an ApiResponseGet.
	 * 
	 * @throws JsonParseException
	 */
	private ApiResponseGet convertResponseGet(String json)
			throws JsonSyntaxException, JsonIOException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Product.class, new ProductDeserializer());
		Gson gson = builder.create();
		try {
			return gson.fromJson(json, ApiResponseGet.class);
		} catch (JsonParseException e) {
			throw new JsonParseException("Error parsing JSON: " + json, e);
		}
	}

	/**
	 * Convert a JSON reader response to an ApiResponse.
	 * 
	 * @throws JsonParseException
	 */
	private ApiResponse convertResponseSend(String json)
			throws JsonSyntaxException, JsonIOException {
		try {
			return new Gson().fromJson(json, ApiResponse.class);
		} catch (JsonParseException e) {
			throw new JsonParseException("Error parsing JSON: " + json, e);
		}
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
			Log.e("ServerHelper", "Error code: " + response.getErrorCode()
					+ " Message: " + response.getMessage());
			if (response.getErrorCode() >= 40 && response.getErrorCode() < 50) {
				throw new AuthenticationException("(" + response.getErrorCode()
						+ ") " + response.getMessage());
			}
			return false;
		}

		Log.d("ServerHelper", "Successful reponse from server.");
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
			URL url;
			try {
				// Download file from web server and save it on internal
				// storage.
				url = new URL(serverPath + PICTURES_FOLDER
						+ i.getServerImagePath());
				HttpsURLConnection connection = (HttpsURLConnection) url
						.openConnection();
				connection.setSSLSocketFactory(sslContext.getSocketFactory());
				InputStream is = connection.getInputStream();
				OutputStream os = App.getContext().openFileOutput(
						i.getImagePath(), Context.MODE_PRIVATE);

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
					App.getContext().deleteFile(i.getImagePath());
				}
				removedOrders.add(o);
			}
		}

		// removes all deprecated orders
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
						&& !newI.getServerImagePath().equals(oldI.getServerImagePath())) {
					App.getContext().deleteFile(oldI.getImagePath());
					saveImage(newI);
				} else if (newI.getId() == oldI.getId()) {
					break;
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

		public int getErrorCode() {
			return errorCode;
		}

		public String getMessage() {
			return message;
		}
	}
}
