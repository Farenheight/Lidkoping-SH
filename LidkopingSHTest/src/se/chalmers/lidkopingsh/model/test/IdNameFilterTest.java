package se.chalmers.lidkopingsh.model.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.lidkopingsh.database.OrderDbFiller;
import se.chalmers.lidkopingsh.model.IdNameFilter;
import se.chalmers.lidkopingsh.model.Order;

public class IdNameFilterTest {
	private List<Order> orders;
	private List<Order> originalOrders;
	private IdNameFilter filter;

	@Before
	public void setUp() {
		filter = new IdNameFilter();
		orders = new ArrayList<Order>();
		originalOrders = new ArrayList<Order>();
	}

	/**
	 * Filter should give no results if id name is not matching the constraint
	 */
	@Test
	public void testNotMathcing() {
		String testConstraint = "Not a matching string";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		originalOrders.addAll(orders);
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "The order should not pass this constraint";
		assertFalse(assertMessage, result);
	}

	/**
	 * Filter should match part of constraint
	 */
	@Test
	public void testPartOfConstraint() {
		String testConstraint = "O";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Should pass this contraint";
		assertTrue(assertMessage, result);
	}

	/**
	 * Filter should only match beginning of constraint
	 */
	@Test
	public void testNotInTheBeginning() {
		String testConstraint = "S";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		originalOrders.addAll(orders);
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Should not pass this contraint";
		assertFalse(assertMessage, result);
	}

	/**
	 * Filter should be letter case independent
	 */
	@Test
	public void testLetterCase() {
		String testConstraint = "o.s";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Should pass";
		assertTrue(assertMessage, result);
	}

	/**
	 * Filter should not care about dots
	 */
	@Test
	public void testDotIndependece() {
		String testConstraint = "os";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Constraint: \"" + testConstraint
				+ "\" should pass the idName: \"O.S\"";
		assertTrue(assertMessage, result);
	}

	/**
	 * Empty string should match everything
	 */
	@Test
	public void testEmptyString() {
		String testConstraint = "";

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Everything should pass empty string";
		assertTrue(assertMessage, result);
	}

	/**
	 * Null should match everything
	 */
	@Test
	public void testNull() {
		String testConstraint = null;

		Order order = OrderDbFiller.getOrderFullyPopulated("O.S");
		
		boolean result = filter.passesFilter(order, testConstraint);

		String assertMessage = "Everything should pass null";
		assertTrue(assertMessage, result);
	}

}
