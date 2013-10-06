package se.chalmers.lidkopingsh;

import se.chalmers.lidkopingsh.model.Order;

public interface IModelFilter<T> {
	public boolean passesFilter(Order order, String constraint);
}
