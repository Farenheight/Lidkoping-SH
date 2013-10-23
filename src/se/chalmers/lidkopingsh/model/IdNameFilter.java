package se.chalmers.lidkopingsh.model;

import java.util.Locale;

public class IdNameFilter implements Filter {

	/**
	 * Checks if the specified order's idName starts with the constraint. It's case
	 * independent and skips dots. For example, an order with the idName O.R.
	 * will pass the constraint "or".
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean passesFilter(Order order, String constraint) {
		
		// Null and empty string should match everything
		if(constraint == null || constraint == "") {
			return true;
		}
		
		// Same case
		String idName = order.getIdName().toUpperCase(Locale.getDefault());
		String constr = constraint.toString().toUpperCase(Locale.getDefault());
		
		// Removes dots
		idName = idName.replaceAll("\\.", "");
		constr = constr.replaceAll("\\.", "");
		
		return idName.startsWith(constr);
	}

}
