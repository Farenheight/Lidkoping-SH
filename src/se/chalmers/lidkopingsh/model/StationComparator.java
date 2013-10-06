package se.chalmers.lidkopingsh.model;

import java.util.Comparator;

/**
 * A Comparator that orders a list of {@link Order}'s depending on how far is
 * left to that station. If two {@link Order} are on the same station, the
 * oldest order will be prioritized.
 * 
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
		if(getPriority(arg0) == getPriority(arg1)){
			if(arg0.getOrderDate() == arg1.getOrderDate()){
				return arg0.getIdName().compareTo(arg1.getIdName());
			}else{				
				// prioritize older orders
				return arg0.getOrderDate() < arg1.getOrderDate() ? 1 : -1;
			}
		}
		if (getPriority(arg0) > getPriority(arg1)) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Get the number of stations left in this Order for any Products task to
	 * come to the same Station as the Station in this Comparator.
	 * Integer.MAX_VALUE if all of the Product's in this order already passed
	 * that station or doesn't have it in there list.
	 * 
	 * @return
	 */
	private int getPriority(T o) {
		return o.getNumOfStationsLeft(station);
	}
	public Station getStation() {
		return station;
	}
}
