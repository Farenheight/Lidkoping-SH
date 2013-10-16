package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.OrderChangedEvent;
import se.chalmers.lidkopingsh.util.Listener;
import se.chalmers.lidkopingsh.util.NetworkUpdateListener;

public interface ILayer extends Listener<OrderChangedEvent> {
	/**
	 * Returns the model that the layer holds
	 * 
	 * @return The model that the layer holds
	 */
	public IModel getModel();
	public void update(boolean getAll);
	public void addNetworkListener(NetworkUpdateListener listener);
	public void removeNetworkListener(NetworkUpdateListener listener);
}
