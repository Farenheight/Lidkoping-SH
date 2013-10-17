package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.IModel;

public interface ILayer {
	/**
	 * Returns the model that the layer holds
	 * 
	 * @return The model that the layer holds
	 */
	public IModel getModel();
}
