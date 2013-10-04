package se.chalmers.lidkopingsh.model;

@Deprecated
public interface IModelFilter<T> {
	public boolean passesFilter(Order order, String constraint);
}
