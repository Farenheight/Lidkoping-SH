package com.example.model;
/**
 * When a class needs to listen when tasks are changed on a product. That class should implement this interface.
 * @author Robin Gronberg
 *
 */
public interface ProductListener {
	/**
	 * Whenever tasks are changed and needs to be synched with a local database, notify that something has changed
	 * @param product
	 */
	public void tasksChanged(Product product);
}
