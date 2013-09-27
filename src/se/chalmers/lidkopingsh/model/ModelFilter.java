package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModelFilter implements IModelFilter<Order> {

	@Override
	public List<Order> getOrdersByFilter(CharSequence constraint, List<Order> orders) {
		if (constraint != null && constraint.length() != 0) {
			// Filtering logic
			List<Order> newOrderList = new ArrayList<Order>();

			for (Order order : orders) {
				String idName = order.getIdName().toUpperCase(Locale.getDefault());
				String constr = constraint.toString().toUpperCase(Locale.getDefault());
				idName = idName.replaceAll("\\.", ""); //Removes dots
				constr = constr.replaceAll("\\.", ""); //Removes dots
				if (idName.startsWith(constr))
					newOrderList.add(order);
			}
			return newOrderList;
		} else {
			// If no input, everything returned
			return orders;
		}
	}

}
