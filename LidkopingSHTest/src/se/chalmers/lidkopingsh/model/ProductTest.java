package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import se.chalmers.lidkopingsh.util.Listener;

public class ProductTest extends TestCase implements Listener<Product> {

	@Test
	public void testAddTask() {
		// Add task 0 to 9 to product and set status to done if tasknumber is
		// even. Every task is added in first place

		/*
		 * Name Status
		 * 
		 * task9 NOT_DONE task8 DONE task7 NOT_DONE task6 DONE task5 NOT_DONE
		 * task4 DONE task3 NOT_DONE task2 DONE task1 NOT_DONE task0 DONE
		 */
		Product p = new Product(new ArrayList<Task>());
		Task[] tasks = new Task[10];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = new Task(new Station(i,"Station" + i), i % 2 == 0 ? Status.DONE
					: Status.NOT_DONE);
			p.addTask(tasks[i], 0);
		}
		{
			int i = tasks.length;
			for (Task task : p.getTasks()) {
				assertEquals(true, (task.equals(new Task(new Station(--i,"Station" + i),
						i % 2 == 0 ? Status.DONE : Status.NOT_DONE))));
			}
		}
		/*
		 * Name Status
		 * 
		 * task0 DONE task1 NOT_DONE task2 DONE task3 NOT_DONE task4 DONE task5
		 * NOT_DONE task6 DONE task7 NOT_DONE task8 DONE task9 NOT_DONE
		 */
		p = new Product(new ArrayList<Task>());
		tasks = new Task[10];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = new Task(new Station(i,"Station" + i), i % 2 == 0 ? Status.DONE
					: Status.NOT_DONE);
			p.addTask(tasks[i], -1);
		}
		{
			int i = 0;
			for (Task task : p.getTasks()) {
				assertEquals(true, (task.equals(new Task(new Station(i,"Station" + i),
						i % 2 == 0 ? Status.DONE : Status.NOT_DONE))));
				i++;
			}
		}
	}

	boolean hasBeenNotified = false;

	@Test
	public void testListener() {
		hasBeenNotified = false;
		Product p = new Product(new ArrayList<Task>());
		Task t = new Task(new Station(0,"Station0"));
		p.addTask(t, 0);
		p.addProductListener(this);
		t.setStatus(Status.DONE);
		assertEquals(true, hasBeenNotified);

		// should be notified when removing task
		hasBeenNotified = false;
		p.removeTask(t);
		assertEquals(true, hasBeenNotified);

		// should not be notified when removing a task
		// that it doesn't have
		hasBeenNotified = false;
		p.removeTask(t);
		assertEquals(false, hasBeenNotified);

		// should be notified when adding a task
		hasBeenNotified = false;
		p.addTask(t, -1);
		assertEquals(true, hasBeenNotified);

		// should not be notified when adding a task
		// that it does have
		hasBeenNotified = false;
		p.addTask(t, -1);
		assertEquals(false, hasBeenNotified);
	}

	@Override
	public void changed(Product product) {
		hasBeenNotified = true;
	}

	@Test
	public void testEquals() {
		List<Task> tasks1 = new ArrayList<Task>();
		tasks1.add(new Task(new Station(0, "Polering")));
		tasks1.add(new Task(new Station(1, "Rengoring")));
		Product p1 = new Product(0, "Blue and cool", "Description", "Plain", tasks1, null);
		
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task(new Station(0, "Polering")));
		tasks2.add(new Task(new Station(1, "Rengoring")));
		Product p2 = new Product(0, "Blue and cool", "Description", "Plain", tasks2, null);
		
		List<Task> tasks3 = new ArrayList<Task>();
		tasks3.add(new Task(new Station(2, "Polering")));
		tasks3.add(new Task(new Station(1, "Rengoring")));
		Product p3 = new Product(0, "Blue and cool", "Description", "Plain", tasks3, null);
		
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
		assertFalse(p2.equals(p3));
	}
}
