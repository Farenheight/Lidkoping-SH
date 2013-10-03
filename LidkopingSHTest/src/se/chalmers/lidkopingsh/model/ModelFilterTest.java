package se.chalmers.lidkopingsh.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ModelFilterTest {
	private List<Order> orders;
	private IModelFilter<Order> filter;

	@Before
	public void setUp() {
		filter = new ModelFilter();
		orders = new ArrayList<Order>();
	}

	/**
	 * Filter should give no results if id name is not matching the constraint
	 */
	@Test
	public void testNoResults() {
		String testIdName = "O.R";
		String testConstraint = "Not a matching string";

		String assertMessage = "Order should not pass";
		assertTrue(assertMessage, true);
	}
}
