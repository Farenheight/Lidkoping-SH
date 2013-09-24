package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

	@Test
	public void testGetters() {
		Customer c = new Customer("Mr", "Olle Bengtsson", "Testvagen 52",
				"416 72 Goteborg", "olle.bengtsson@testuser.com", 0);
		assertTrue(c.getTitle().equals("Mr"));
		assertTrue(c.getName().equals("Olle Bengtsson"));
		assertTrue(c.getAddress().equals("Testvagen 52"));
		assertTrue(c.getPostAddress().equals("416 72 Goteborg"));
		assertTrue(c.getEMail().equals("olle.bengtsson@testuser.com"));
	}

	@Test
	public void testEquals() {
		Customer c1 = new Customer("Mr", "Olle Bengtsson", "Testvagen 52",
				"416 72 Goteborg", "olle.bengtsson@testuser.com", 0);
		Customer c2 = new Customer("Mr", "Olle Bengtsson", "Testvagen 52",
				"416 72 Goteborg", "olle.bengtsson@testuser.com", 0);
		assertTrue(c1.equals(c2));
		assertTrue(c2.equals(c1));
	}

}
