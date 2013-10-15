package se.chalmers.lidkopingsh;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.chalmers.lidkopingsh.model.Order;

/**
 * 
 * TODO: Refactor into an android Filter
 * @author Simon Bengtsson
 *
 */
public class ModelFilter {

	public boolean passesFilter(Order order, String constraint) {
		String idName = order.getIdName().toUpperCase(Locale.getDefault());
		String constr = constraint.toString().toUpperCase(Locale.getDefault());
		idName = idName.replaceAll("\\.", ""); // Removes dots
		constr = constr.replaceAll("\\.", ""); // Removes dots
		return idName.startsWith(constr);
	}

	public List<Order> getOrdersByFilter(CharSequence constraint, List<Order> orders,
			List<Order> originalObjects) {
		if (constraint == null || constraint.length() == 0) {
			return new ArrayList<Order>(originalObjects);
		} else {
			constraint = constraint.toString();
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
