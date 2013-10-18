package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.database.ILayer;
import se.chalmers.lidkopingsh.database.OrderDbLayer;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.server.ServerConnector;
import android.content.Context;

/**
 * Used for accessing and creating model, database layer and server connector.
 * This acts as a central access point for all controllers (activities).
 * 
 * @author Anton Jansson
 * @author Simon Bengtsson
 */
class Accessor {
	private static IModel model;
	private static ServerConnector server;
	private static ILayer layer;

	/**
	 * Returns the model used in the application containing all orders. Creates
	 * this model from the database the first time this method is called.
	 * 
	 * @param context
	 *            Context used for using database.
	 * @return the model with all orders.
	 */
	public synchronized static IModel getModel(Context context) {
		if (model == null) {
			model = getLayer(context).getModel(); // Should be populated from DB
			model.addDataSyncedListener(layer);
		}
		return model;
	}

	/**
	 * Returns the layer that creates the model.
	 * 
	 * @param context
	 *            Context for using database.
	 * @return A layer that can create a model.
	 */
	private synchronized static ILayer getLayer(Context context) {
		if (layer == null) {
			layer = new OrderDbLayer(context);
		}
		return layer;
	}

	/**
	 * Returns a connector object for the remote server.
	 * <p>
	 * {@link #getModel(Context)} must be called before this method.
	 * 
	 * @param context
	 *            Context for accessing local storage.
	 * @param model
	 *            The model holding all orders.
	 * @return A connector that can connect to the server.
	 */
	public synchronized static ServerConnector getServerConnector(Context context) {
		// TODO: Create interface for ServerConnector
		if (server == null) {
			if (model == null) {
				throw new IllegalStateException(
						"Model is not created. getModel(Context) must be called before this method.");
			}
			server = new ServerConnector(context, model);
			server.addOrderChangedListener(model);
			model.addOrderChangedListener(server);
			server.update(true);
		}
		return server;
	}
}
