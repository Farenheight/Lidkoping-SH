package se.chalmers.lidkopingsh.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 *TODO: Comment
 */
public class TestStationComparator {
	
	private Station station0;
	private Station station1;
	private Station station2;
	private Order otherOrder;
	private Order order;
	private StationComparator<Order> c;

	@Before
	public void setUp(){
		order = new Order(0,"","1",0,0,"","","","",0,new Customer("","","","","",0),null,null);
		otherOrder = new Order(0,"","1",0,0,"","","","",0,new Customer("","","","","",0),null,null);
		station0 = new Station(0, "Station0");
		station1 = new Station(1, "Station1");
		station2 = new Station(2, "Station2");
	}
	
	@Test
	public void testNoOrderDone() {
		order.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.NOT_DONE),
				new Task(station1, Status.NOT_DONE),
				new Task(station2, Status.NOT_DONE)
		})));
		otherOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.NOT_DONE),
				new Task(station2, Status.DONE)
		})));
		c = new StationComparator<Order>(station1);
		assertTrue("First order has no tasks done, second all but the current.", 
				c.compare(order, otherOrder) == 0);
		assertTrue("First order has all but the current task done, second has none.", 
				c.compare(otherOrder, order) == 0);
	}

	@Test
	public void testBothOrdersDone() {
		order.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.NOT_DONE),
				new Task(station2, Status.NOT_DONE)
		})));
		otherOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.DONE),
				new Task(station2, Status.DONE)
		})));
		c = new StationComparator<Order>(station0);
		assertTrue("First order has one done, second order has all.", 
				c.compare(order, otherOrder) == 0);
		assertTrue("First order has all done, second order has one.", 
				c.compare(otherOrder, order) == 0);
	}
	
	/**
	 * One order already been to the station tested. The other one hasen't.
	 */
	@Test
	public void testOneOrderDone() {
		order.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.DONE),
				new Task(station2, Status.NOT_DONE)
		})));
		otherOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.NOT_DONE),
				new Task(station1, Status.DONE),
				new Task(station2, Status.DONE)
		})));
		c = new StationComparator<Order>(station2);
		assertTrue("First order is not done.", 
				c.compare(order, otherOrder) == -1);
		assertTrue("First order is done.", 
				c.compare(otherOrder, order) == 1);
	}
	
	/**
	 * 
	 */
	@Test
	public void testOneStationDifference(){
		order.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.NOT_DONE),
				new Task(station2, Status.NOT_DONE)
		})));
		otherOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0, Status.DONE),
				new Task(station1, Status.DONE),
				new Task(station2, Status.NOT_DONE)
		})));
		c = new StationComparator<Order>(station2);
		assertTrue("otherOrder is closest to station2", 
				c.compare(order, otherOrder) == -1);
		assertTrue("otherOrder is closest to station2", 
				c.compare(otherOrder, order) == 1);
		
	}
	
	@Test
	public void test() {
		Station station0 = new Station(0, "Station0");
		Station station1 = new Station(1, "Station1");
		Station station2 = new Station(2, "Station2");
		
		
		Order order0 = new Order(0,"","0",0,0,"","","","",0,new Customer("","","","","",0),null,null);
		order0.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0),
				new Task(station1),
				new Task(station2)
				})));
		Order order1 = new Order(0,"","1",0,0,"","","","",0,new Customer("","","","","",0),null,null);
		order1.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0,Status.DONE),
				new Task(station1),
				new Task(station2)
		})));
		Order order2 = new Order(0,"","2",0,0,"","","","",0,new Customer("","","","","",0),null,null);
		order2.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0,Status.DONE),
				new Task(station1,Status.DONE),
				new Task(station2)
		})));
		Order[] orders = new Order[]{order0,order1,order2};
		Arrays.sort(orders, new StationComparator<Order>(station0));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order0,order1,order2})));
		Arrays.sort(orders, new StationComparator<Order>(station1));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order1,order0,order2})));
		Arrays.sort(orders, new StationComparator<Order>(station2));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order2,order1,order0})));
		StationComparator<Order> comp = new StationComparator<Order>(station0);
		assertFalse(comp.compare(order1, order0) == comp.compare(order0, order1));
		
	}

}
