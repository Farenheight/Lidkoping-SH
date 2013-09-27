package se.chalmers.lidkopingsh.model;

import java.util.List;

public interface IModelFilter<T> {
	public List<T> getOrdersByFilter(CharSequence constraint,
			List<T> items);
}
