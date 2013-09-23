package com.example.lidkopingsh.model;

public class ModelHandler {
	private static IModel model;
	private static ILayer layer;
	
	public static IModel getModel(){
		if(model == null){
			model = getLayer().getModel(); // Should be populated from DB
		}
		return model;
	}
	
	public static ILayer getLayer(){
		if(layer == null){
			layer = new ExampleLayer();
		}
		return layer;
	}
}
