package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.util.Listener;

public interface ILayer extends Listener<Order> {
	/**
	 * Returns the model that the layer holds
	 * 
	 * @return The model that the layer holds
	 */
	public IModel getModel();
}
