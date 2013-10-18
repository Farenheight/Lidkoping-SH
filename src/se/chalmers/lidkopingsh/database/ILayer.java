package se.chalmers.lidkopingsh.database;

import se.chalmers.lidkopingsh.model.DataChangedListener;
import se.chalmers.lidkopingsh.model.IModel;

public interface ILayer extends DataChangedListener {
	/**
	 * Returns the model that the layer holds
	 * 
	 * @return The model that the layer holds
	 */
	public IModel getModel();
}
