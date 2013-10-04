package se.chalmers.lidkopingsh.model;

public interface ILayer extends Listener<Order> {
	/**
	 * Returns the model that the layer holds
	 * 
	 * @return The model that the layer holds
	 */
	public IModel getModel();
	public void updateDatabase(Order o);
}
