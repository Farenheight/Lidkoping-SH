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
	
	
//TODO: Implemented something like this for each field instead of the above	
	
//	// First match against the whole, non-splitted value
//	if (valueText.startsWith(lcConstraint)) {
//		newValues.add(order);
//	} else {
//		final String[] words = valueText.split(" ");
//		final int wordCount = words.length;
//
//		// Start at index 0, in case valueText starts with
//		// space(s)
//		for (int k = 0; k < wordCount; k++) {
//			if (words[k].startsWith(lcConstraint)) {
//				newValues.add(order);
//				break;
//			}
//		}
//	}
	

}
