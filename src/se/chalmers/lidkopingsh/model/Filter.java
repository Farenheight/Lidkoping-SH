package se.chalmers.lidkopingsh.model;

public interface Filter {
	
	/**
	 * Checks if an individual order passes the current constraint.
	 * 
	 * @param order
	 *            The order to be matched
	 * @param constraint
	 *            The string match against
	 * @return If the order passes the filter
	 */
	boolean passesFilter(Order order, String constraint);
}
