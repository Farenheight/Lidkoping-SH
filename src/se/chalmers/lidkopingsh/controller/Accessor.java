package se.chalmers.lidkopingsh.controller;

import se.chalmers.lidkopingsh.database.ILayer;
import se.chalmers.lidkopingsh.database.OrderDbLayer;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.server.ServerConnector;

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
	 * @return the model with all orders.
	 */
	public synchronized static IModel getModel() {
		if (model == null) {
			model = getLayer().getModel(); // Should be populated from DB
			model.addDataSyncedListener(layer);
		}
		return model;
	}

	/**
	 * Returns the layer that creates the model.
	 * 
	 * @return A layer that can create a model.
	 */
	private synchronized static ILayer getLayer() {
		if (layer == null) {
			layer = new OrderDbLayer();
		}
		return layer;
	}

	/**
	 * Returns a connector object for the remote server.
	 * <p>
	 * {@link #getModel()} must be called before this method.
	 * 
	 * @return A connector that can connect to the server.
	 */
	public synchronized static ServerConnector getServerConnector() {
		// TODO: Create interface for ServerConnector
		if (server == null) {
			if (model == null) {
				throw new IllegalStateException(
						"Model is not created. getModel() must be called before this method.");
			}
			server = new ServerConnector(model);
			server.addOrderChangedListener(model);
			model.addOrderChangedListener(server);
			server.update(true);
		}
		return server;
	}
}
