package se.chalmers.lidkopingsh.model;

import java.util.Locale;

import android.util.Log;

public class ModelFilter implements IModelFilter<Order> {

	@Override
	public boolean passesFilter(Order order, String constraint) {
		String idName = order.getIdName().toUpperCase(Locale.getDefault());
		String constr = constraint.toString().toUpperCase(Locale.getDefault());
		idName = idName.replaceAll("\\.", ""); // Removes dots
		constr = constr.replaceAll("\\.", ""); // Removes dots
		Log.d("DEBUG", "" + idName.startsWith(constr));
		return idName.startsWith(constr);
	}

}
