package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.server.ServerConnector;
import android.content.Context;

public class ModelHandler {
	private static IModel model;
	private static ServerConnector server;

	public static IModel getModel(Context context) {
		if (model == null) {
			OrderDbLayer layer = new OrderDbLayer(context);
			model = layer.getModel(); // Should be populated from DB
			getServerConnector(context).addOrderChangedListener(model);
			model.addDataChangedListener(layer);
			model.addOrderChangedListener(server);
			server.update(true);
		}
		return model;
	}

	public static ServerConnector getServerConnector(Context context) {
		// TODO: Create interface for ServerConnector
		if (server == null) {
			server = new ServerConnector(context);
		}
		return server;
	}

	public static void update(boolean getAll) {
		server.update(true);
	}
}
