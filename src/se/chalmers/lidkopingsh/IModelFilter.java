package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.model.Order;

@Deprecated
public interface IModelFilter<T> {
	public boolean passesFilter(Order order, String constraint);
}
