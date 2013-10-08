package se.chalmers.lidkopingsh;

import java.util.Collection;

import se.chalmers.lidkopingsh.handler.ModelHandler;
import se.chalmers.lidkopingsh.model.Order;
import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity {

	private Collection<Order> mOrderList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_root);
		mOrderList = ModelHandler.getModel(this).getOrders();
	}
}
