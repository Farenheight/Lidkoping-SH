package se.chalmers.lidkopingsh.model;

public interface ILayer extends Listener<Order> {
	public IModel getModel();
	public void updateDatabase(Order o);
}
