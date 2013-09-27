package se.chalmers.lidkopingsh.model;

import java.util.List;

public interface IModelFilter<T> {
	public boolean passesFilter(Order order, String constraint);
}
