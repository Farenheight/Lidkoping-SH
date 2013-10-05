package se.chalmers.lidkopingsh.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class TestStationComparator {

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
//		System.out.print(Arrays.asList(orders));
		StationComparator<Order> comp = new StationComparator<Order>(station0);
		assertFalse(comp.compare(order1, order0) == comp.compare(order0, order1));
		
	}

}
