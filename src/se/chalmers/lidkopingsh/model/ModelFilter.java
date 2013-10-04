package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModelFilter implements IModelFilter<Order> {

	@Override
	public boolean passesFilter(Order order, String constraint) {
		String idName = order.getIdName().toUpperCase(Locale.getDefault());
		String constr = constraint.toString().toUpperCase(Locale.getDefault());
		idName = idName.replaceAll("\\.", ""); // Removes dots
		constr = constr.replaceAll("\\.", ""); // Removes dots
		return idName.startsWith(constr);
	}

	public List<Order> getOrdersByFilter(String constraint, List<Order> orders,
			List<Order> originalObjects) {
		if (constraint == null || constraint.length() == 0) {
			return new ArrayList<Order>(originalObjects);
		} else {
			ArrayList<Order> orderList = new ArrayList<Order>(originalObjects);
			final ArrayList<Order> newValues = new ArrayList<Order>();

			for (Order order : orderList) {
				if (passesFilter(order, constraint.toString())) {
					newValues.add(order);
				}
			}

			return newValues;
		}
	} 

}
