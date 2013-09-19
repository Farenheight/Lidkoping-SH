package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

	@Test
	public void testGetters() {
		Customer c = new Customer("Mr", "Olle Bengtsson", "Testv�gen 52",
				"416 72 G�teborg", "olle.bengtsson@testuser.com");
		assertTrue(c.getTitle().equals("Mr"));
		assertTrue(c.getName().equals("Olle Bengtsson"));
		assertTrue(c.getAddress().equals("Testv�gen 52"));
		assertTrue(c.getPostAddress().equals("416 72 G�teborg"));
		assertTrue(c.getEMail().equals("olle.bengtsson@testuser.com"));
	}

}
