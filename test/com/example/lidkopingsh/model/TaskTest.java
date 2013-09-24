package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;


public class TaskTest {
	
	@Test
	public void testEquals(){
		Task t1 = new Task(1, "Polering");
		Task t2 = new Task(1, "Polering");
		assertTrue(t1.equals(t2));
		
		t2.setStatus(Status.DONE);
		assertFalse(t1.equals(t2));
	}

}
