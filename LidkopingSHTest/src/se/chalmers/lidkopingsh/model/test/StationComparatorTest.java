package se.chalmers.lidkopingsh.model.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.StationComparator;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Task;

public class StationComparatorTest {
	
	private Station firstStation;
	private Station secondStation;
	private Station thirdStation;
	private Order firstOrder;
	private Order secondOrder;
	private Order thirdOrder;
	private StationComparator<Order> c;

	@Before
	public void setUp(){
		// Three orders that has everything the same. Products with 
		// different amount of tasks done added in test.
		firstOrder = new Order(0,"firstOrder","1",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);
		secondOrder = new Order(0,"seconedOrder","1",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);
		thirdOrder = new Order(0,"thirdOrder","1",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);		
		
		firstStation = new Station(0, "Station0");
		secondStation = new Station(1, "Station1");
		thirdStation = new Station(2, "Station2");
	}
	
	/*
	 * NORMAL TESTS 
	 */
	
	/**
	 * Tests that a station that already been to the station has lower 
	 * priority than one that has yet to.
	 */
	@Test
	public void testDoneVSNotDone() {
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE), 
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.DONE)
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE), 
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		c = new StationComparator<Order>(thirdStation);
		int result = c.compare(firstOrder, secondOrder);
		assertTrue("First order has already been to the station, was: " + result, 
				result == 1);
		testMathematicalProperties(firstOrder, secondOrder, c);
	}

	/**
	 * Tests that orders both with a task done has the same priority
	 */
	@Test
	public void testBothOrdersDone() {
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.DONE)
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.DONE)
		})));
		c = new StationComparator<Order>(thirdStation);
		assertTrue("Orders both with a task done should have the same priority", 
				c.compare(firstOrder, secondOrder) == 0);
		testMathematicalProperties(firstOrder, secondOrder, c);
	}
	
	/**
	 * Tests that orders with the same amount of stations to the station 
	 * has the same priority
	 */
	@Test
	public void testSameDistance() {
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		c = new StationComparator<Order>(thirdStation);
		assertTrue("Orders the same amount of stations to the station should have the same priority", 
				c.compare(firstOrder, secondOrder) == 0);
		testMathematicalProperties(firstOrder, secondOrder, c);
	}
	
	/**
	 * Tests that the order closest to the station has highest priority.
	 */
	@Test
	public void testCloserTo(){
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.NOT_DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE),
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		c = new StationComparator<Order>(thirdStation);
		assertTrue("Order closer to the station should have higher priority.", 
				c.compare(firstOrder, secondOrder) == 1);
		testMathematicalProperties(firstOrder, secondOrder, c);
	}
	
	/*
	 * SPECIAL CASE TESTS
	 */
	
	/**
	 * Tests that an order without a product has lower priority 
	 * than one with products that has tasks not done and the same 
	 * priority as one with the task done
	 */
	@Test
	public void testNoProduct() {
		//No product added to first order
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.NOT_DONE)
		})));
		thirdOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.DONE)
		})));
		c = new StationComparator<Order>(firstStation);
		assertTrue("No product should be lower priority than not done", 
				c.compare(firstOrder, secondOrder) == 1);
		testMathematicalProperties(firstOrder, secondOrder, c);
		assertTrue("No product should have the same priority as done", 
				c.compare(firstOrder, thirdOrder) == 0);
		testMathematicalProperties(firstOrder, thirdOrder, c);
	}
	
	/**
	 * Tests that an order with no product that has the task
	 */
	@Test
	public void testNoStation() {
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.NOT_DONE),
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(secondStation, Status.NOT_DONE)
		})));
		thirdOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(secondStation, Status.DONE)
		})));
		
		c = new StationComparator<Order>(secondStation);
		int result = c.compare(firstOrder, secondOrder);
		assertTrue("No station should have lower priority than not done. Was: " + result, 
				result == 1);
		testMathematicalProperties(firstOrder, secondOrder, c);
		
		result = c.compare(firstOrder, thirdOrder);
		assertTrue("No station should have the same priority as done. Was: " + result, 
				result == 0);
		testMathematicalProperties(firstOrder, thirdOrder, c);
	}
	
	/**
	 * Tests that the sort still works if the workers doesn't fill in the 
	 * tasks in order
	 */
	@Test
	public void testRandomCheckOrder() {
		firstOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.NOT_DONE),
				new Task(secondStation, Status.NOT_DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		secondOrder.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(firstStation, Status.NOT_DONE),
				new Task(secondStation, Status.DONE),
				new Task(thirdStation, Status.NOT_DONE)
		})));
		
		c = new StationComparator<Order>(thirdStation);
		assertTrue("One stations left should give higher priority than two", 
				c.compare(firstOrder, secondOrder) == 1);
		testMathematicalProperties(firstOrder, secondOrder, c);
	}
	
	/**
	 * Tests that the comparator's comparison is symmetric and reflexive
	 */
	private void testMathematicalProperties(Order firstOrder,
			Order secondOrder, StationComparator<Order> c) {
		// Symmetric
		if(c.compare(firstOrder, secondOrder) == 0) {
			assertTrue("Should be symmetric", c.compare(firstOrder, secondOrder) ==
					c.compare(secondOrder, firstOrder));
		} else {
		assertTrue("Should be symmetric", c.compare(firstOrder, secondOrder) !=
				c.compare(secondOrder, firstOrder));
		}
		// Reflexive
		assertTrue("Should be reflexive", 
				c.compare(firstOrder, firstOrder) == 0);
		assertTrue("Should be reflexive", 
				c.compare(secondOrder, secondOrder) == 0);		
	}
	
	@Test
	public void testSortArrayOfOrders() {
		Station station0 = new Station(0, "Station0");
		Station station1 = new Station(1, "Station1");
		Station station2 = new Station(2, "Station2");
		
		
		Order order0 = new Order(0,"","0",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);
		order0.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0,Status.NOT_DONE),
				new Task(station1,Status.NOT_DONE),
				new Task(station2,Status.NOT_DONE)
				})));
		Order order1 = new Order(1,"","1",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);
		order1.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0,Status.DONE),
				new Task(station1,Status.NOT_DONE),
				new Task(station2,Status.NOT_DONE)
		})));
		Order order2 = new Order(2,"","2",0,0,"","","","",0,new Customer("","","","","",0),null,null,null);
		order2.addProduct(new Product(Arrays.asList(new Task[]{
				new Task(station0,Status.DONE),
				new Task(station1,Status.DONE),
				new Task(station2,Status.NOT_DONE)
		})));
		Order[] orders = new Order[]{order0,order1,order2};
		Arrays.sort(orders, new StationComparator<Order>(station0));
		System.out.println(Arrays.asList(orders));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order0,order1,order2})));
		Arrays.sort(orders, new StationComparator<Order>(station1));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order1,order0,order2})));
		Arrays.sort(orders, new StationComparator<Order>(station2));
		assertTrue(Arrays.asList(orders).equals(Arrays.asList(new Order[]{order2,order1,order0})));
		StationComparator<Order> comp = new StationComparator<Order>(station0);
		assertFalse(comp.compare(order1, order0) == comp.compare(order0, order1));
		
	}

}
