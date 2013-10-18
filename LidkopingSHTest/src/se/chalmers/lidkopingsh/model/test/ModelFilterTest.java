package se.chalmers.lidkopingsh.model.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.lidkopingsh.ModelFilter;
import se.chalmers.lidkopingsh.database.OrderDbFiller;
import se.chalmers.lidkopingsh.model.Order;

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
		String testConstraint = "Not a matching string";

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
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
		String testConstraint = "O";

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
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
		String testConstraint = "S";

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
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
		String testConstraint = "o.s";

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
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
		String testConstraint = "os";

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
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

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
		orders.add(OrderDbFiller.getOrderFullyPopulated("O.R"));
		orders.add(OrderDbFiller.getOrderFullyPopulated("O.T"));
		
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

		orders.add(OrderDbFiller.getOrderFullyPopulated("O.S"));
		orders.add(OrderDbFiller.getOrderFullyPopulated("O.R"));
		orders.add(OrderDbFiller.getOrderFullyPopulated("O.T"));
		
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
