package se.chalmers.lidkopingsh.model;


public interface IModelFilter<T> {
	public boolean passesFilter(Order order, String constraint);
}
