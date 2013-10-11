package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.IModel;
import android.content.Context;


public class ModelHandler {
	private static IModel model;
	private static ILayer layer;

	public static IModel getModel(Context context) {
		if (model == null) {
			System.out.println("Created new model from db.");
			model = getLayer(context).getModel(); // Should be populated from DB
		}
		return model;
	}

	private static ILayer getLayer(Context context) {
		if (layer == null) {
			layer = new TestLayer();
		}
		return layer;
	}	
}
