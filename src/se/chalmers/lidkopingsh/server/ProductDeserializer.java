package se.chalmers.lidkopingsh.server;

import java.lang.reflect.Type;

import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Stone;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Used for deserializing {@link Product}. Detects if the JSON object should be
 * created as a {@link Stone} or {@link Product}.
 * 
 * @author Anton Jansson
 * 
 */
public class ProductDeserializer implements JsonDeserializer<Product> {

	@Override
	public Product deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Gson gson = new Gson();
		if (json.getAsJsonObject().has("stoneModel")) { // Product is a stone
			return gson.fromJson(json, Stone.class);
		} else {
			return gson.fromJson(json, Product.class);
		}
	}

}
