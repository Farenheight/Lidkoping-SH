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

	/**
	 * Compares the two provided orders for their priority.
	 * 
	 * @param order
	 * @param otherOrder
	 * @return If order has higher priority than otherOrder, 1 is return. 0 if
	 *         the same and -1 if otherOrder has higher priority.
	 * 
	 */
	@Override
	public int compare(T order, T otherOrder) {
		if (getPriority(order) == getPriority(otherOrder)) {
			if (order.getOrderDate() == otherOrder.getOrderDate()) {
				return order.getIdName().compareTo(otherOrder.getIdName());
			} else {
				// prioritize older orders
				return order.getOrderDate() < otherOrder.getOrderDate() ? 1
						: -1;
			}
		}
		if (getPriority(order) > getPriority(otherOrder)) {
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
}
