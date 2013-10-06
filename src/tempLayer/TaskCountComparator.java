package tempLayer;

import java.util.Comparator;
import java.util.List;

import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Task;

/**
 * A Comparator that orders a list depending on how many tasks that is done
 * 
 * @author Simon
 * 
 * @param <T>
 */
public class TaskCountComparator<T extends Order> implements Comparator<T> {

	@Override
	public int compare(T order, T otherOrder) {
		if (getTaskCount(order) >= getTaskCount(otherOrder)) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Returns the number of tasks that is done for the first product on the
	 * specified order
	 */
	private int getTaskCount(T order) {
		List<Task> tasks = order.getProducts().get(0).getTasks();
		int taskCount = 0;
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getStatus().equals(Status.DONE)) {
					++taskCount;
			}
		}
		return taskCount;
	}
}
