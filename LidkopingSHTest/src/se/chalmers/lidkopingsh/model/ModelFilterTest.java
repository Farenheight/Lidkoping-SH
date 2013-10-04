package se.chalmers.lidkopingsh.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.lidkopingsh.ModelFilter;

public class ModelFilterTest {
	private List<Order> orders;
	private List<Order> originalOrders;
	private ModelFilter filter;

	@Before
	public void setUp() {
		filter = new ModelFilter();
		orders = new ArrayList<Order>();
		originalOrders = new ArrayList<Order>();
	}

	/**
	 * Filter should give no results if id name is not matching the constraint
	 */
	@Test
	public void testNoResults() {
		String testIdName = "O.R";
		String testConstraint = "Not a matching string";

		orders.add(new Order(0, null, testIdName, 0, 0, null, null, null, null,
				0, new Customer("","","","","",0), null,null));
		originalOrders.addAll(orders);
		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 0, is:"
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 0);
	}

	/**
	 * Filter should match part of constraint
	 */
	@Test
	public void testPartOfConstraint() {
		String testIdName = "O.R";
		String testConstraint = "O";

		orders.add(new Order(0, null, testIdName, 0, 0, null, null, null, null,
				0, new Customer("","","","","",0), null,null));
		originalOrders.addAll(orders);
		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 1, is:"
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 1);
	}

	/**
	 * Filter should only match beginning of constraint
	 */
	@Test
	public void testNotInTheBeginning() {
		String testIdName = "O.SS";
		String testConstraint = "SS";

		orders.add(new Order(0, null, testIdName, 0, 0, null, null, null, null,
				0, new Customer("","","","","",0), null,null));
		originalOrders.addAll(orders);
		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 0, is:"
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 0);
	}

	/**
	 * Filter should be letter case independent
	 */
	@Test
	public void testLetterCase() {
		String testIdName = "O.R";
		String testConstraint = "o.r";

		orders.add(new Order(0, null, testIdName, 0, 0, null, null, null, null,
				0, new Customer("","","","","",0), null,null));
		originalOrders.addAll(orders);
		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 1, is:"
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 1);
	}

	/**
	 * Filter should not care about dots
	 */
	@Test
	public void testDotIndependece() {
		String testIdName = "O.R";
		String testConstraint = "or";

		orders.add(new Order(0, null, testIdName, 0, 0, null, null, null, null,
				0, new Customer("","","","","",0), null,null));
		originalOrders.addAll(orders);
		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 1, is:"
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 1);
	}

	/**
	 * Empty string should match everything
	 */
	@Test
	public void testEmptyString() {
		String testConstraint = "";

		orders.add(new Order(0, null, "O.R", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		orders.add(new Order(0, null, "O.S", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		orders.add(new Order(0, null, "S.S", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		
		originalOrders.addAll(orders);

		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 3, is: "
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 3);
	}

	/**
	 * Null should match everything
	 */
	@Test
	public void testNull() {
		String testConstraint = null;

		orders.add(new Order(0, null, "O.R", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		orders.add(new Order(0, null, "O.S", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		orders.add(new Order(0, null, "S.S", 0, 0, null, null, null, null, 0,
				new Customer("","","","","",0), null,null));
		
		originalOrders.addAll(orders);

		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 3, is: "
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 3);
	}

	/**
	 * Emty Order list should return nothing
	 */
	@Test
	public void testEmptyOrderList() {
		String testConstraint = null;
		
		originalOrders.addAll(orders);

		List<Order> returnedOrderList = filter.getOrdersByFilter(
				testConstraint, orders, originalOrders);

		String assertMessage = "Size of list should be 0, is: "
				+ returnedOrderList.size();
		assertTrue(assertMessage, returnedOrderList.size() == 0);
	}

}
