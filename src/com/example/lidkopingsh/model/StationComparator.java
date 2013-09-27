package com.example.lidkopingsh.model;

import java.util.Comparator;
/**
 * A Comparator that orders a list of {@link Order}'s depending on how far is left to that station
 * @author Robin Gronberg
 *
 * @param <T>
 */
public class StationComparator<T extends Order> implements Comparator<T> {
	private final Station station;

	public StationComparator(Station station) {
		this.station = station;
	}

	@Override
	public int compare(T arg0, T arg1) {
		if(getPriority(arg0) < getPriority(arg1)){
			return 1;
		}
		if(getPriority(arg0) > getPriority(arg1)){
			return -1;
		}else{
			return 0;
		}
	}

	/**
	 * Get the number of stations left in this Order for any Products task to
	 * come to the same Station as the Station in this Comparator. -1 if all of
	 * the Product's in this order already passed that station or doesn't have it
	 * in there list.
	 * 
	 * @return 
	 */
	private int getPriority(T o) {
		for(Product p : o.getProducts()){
			int i = 0;
			for(Task t : p.getTasks()){
				if (t.getStatus().equals(Status.NOT_DONE)){
					if(t.getStation().equals(station)){
						return i;						
					}else {
						i++;
					}
				}
			}
		}
		return -1;
	}
}
