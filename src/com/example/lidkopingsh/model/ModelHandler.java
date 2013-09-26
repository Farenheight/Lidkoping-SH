package com.example.lidkopingsh.model;

import android.content.Context;

import com.example.lidkopingsh.database.OrderDbLayer;

public class ModelHandler {
	private static IModel model;
	private static ILayer layer;

	public static IModel getModel(Context context) {
		if (model == null) {
			model = getLayer(context).getModel(); // Should be populated from DB
		}
		return model;
	}

	public static ILayer getLayer(Context context) {
		if (layer == null) {
			layer = new OrderDbLayer(context);
		}
		return layer;
	}
}
