package se.chalmers.lidkopingsh.handler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.IModel;
import se.chalmers.lidkopingsh.model.MapModel;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;

/**
 * Creates a dummy model mainly for sort tests. Containing orders with tasks by
 * the following pattern:
 * 
 * Order TASK_COUNT: TASK_COUNT tasks done Order TASK_COUNT + 1: 0 tasks done
 * Order TASK_COUNT + 2: 1 tasks done Order TASK_COUNT + n: n - 1 tasks done
 * 
 * Ex. TASK_COUNT == 2
 * 
 * Order 0: 0 task done Order 1: 1 task done Order 2: 2 tasks done Order 3: 0
 * tasks done Order 4: 1 tasks done etc
 * 
 * @author Simon
 * 
 */
public class TestLayer implements ILayer {

	private static int TASK_COUNT = 4; // And station count
	private static int ORDER_COUNT = 10;

	@Override
	public void changed(Order object) {
		// Does nothing
	}

	@Override
	public IModel getModel() {
		List<Station> stations = new ArrayList<Station>();
		for (int i = 0; i < TASK_COUNT; i++) {
			stations.add(new Station(i, "Station " + i));
		}
		Collection<Order> orders = new ArrayList<Order>();
		for (int i = 0; i < ORDER_COUNT; i++) {
			orders.add(generateOrder(stations));
		}
		MapModel m = new MapModel(orders, stations);
		return m;
	}

	private static int count = 0;

	private Order generateOrder(List<Station> stations) {
		ArrayList<Product> pList = new ArrayList<Product>();
		Product p = new Stone(1, null, null, null, null, null, null, null,
				null, null);
		// The tasks is going to be
		int doneCount = count % (TASK_COUNT + 1);
		p.addTasks(genTasks(stations, doneCount));
		pList.add(p);

		Order order = new Order(count, "1200", "O."
				+ String.valueOf((char) (count + 65)),
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogard", "Kyrkonamnd", "Kyrkogardsblock",
				"Kyrkogardsnummer", Long.parseLong("1371679200000"),
				new Customer("Mr", "Olle Bengtsson", "Testvagen 52",
						"416 72 Goteborg", "olle.bengtsson@testuser.com",
						(int) System.currentTimeMillis()), pList, null);
		pList.add(new Stone(1, null, null, null, null, null, null, null, null,
				null));
		++count;

		return order;
	}

	/**
	 * Returns a list with the same amount of tasks as {stations} and
	 * {doneCount} of those tasks are marked as done.
	 */
	private List<Task> genTasks(List<Station> stations, int doneCount) {
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < TASK_COUNT; i++) {
			if (i < doneCount) {
				tasks.add(new Task(stations.get(i), Status.DONE));
			} else {
				tasks.add(new Task(stations.get(i), Status.NOT_DONE));
			}
		}
		return tasks;
	}

	public static void main(String[] args) {
		new TestLayer().getModel();
	}

}