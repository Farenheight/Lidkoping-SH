package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

	@Test
	public void testGetters() {
		Customer c = new Customer("Mr", "Olle Bengtsson", "Testvägen 52",
				"416 72 Göteborg", "olle.bengtsson@testuser.com");
		assertTrue(c.getTitle().equals("Mr"));
		assertTrue(c.getName().equals("Olle Bengtsson"));
		assertTrue(c.getAddress().equals("Testvägen 52"));
		assertTrue(c.getPostAddress().equals("416 72 Göteborg"));
		assertTrue(c.getEMail().equals("olle.bengtsson@testuser.com"));
	}

}
