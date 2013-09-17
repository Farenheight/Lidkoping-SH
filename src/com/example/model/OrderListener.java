package com.example.model;
/**
 * When a class needs to listen when orders are changed. That class should implement this interface.
 * @author Kim
 *
 */
public interface OrderListener {
	/**
	 * Whenever orders are changed and needs to be synched with a local database, notify that something has changed
	 * @param product
	 */
	public void orderChanged(Order order);
}
