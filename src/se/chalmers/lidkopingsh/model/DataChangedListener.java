package se.chalmers.lidkopingsh.model;

import java.util.Collection;

/**
 * Listener for when model data is changed.
 * 
 * @author Anton Jansson
 * @author Simon Bengtsson
 */
public interface DataChangedListener {
	/**
	 * Orders has been changed.
	 * 
	 * @param orders
	 *            The orders that has been added.
	 * @param changed
	 *            The orders that has been changed.
	 * @param removed
	 *            The orders that has been removed.
	 * @param currentModel 
	 * 			  The current Model that is used in the application
	 */
	void ordersChanged(Collection<Order> added, Collection<Order> changed,
			Collection<Order> removed, IModel currentModel);
}