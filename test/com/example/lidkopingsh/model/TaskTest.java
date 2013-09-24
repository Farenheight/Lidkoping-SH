package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest implements Listener<Task>{
	private boolean synced = false;
	@Test
	public void testListener() {
		Task t = new Task(0, "Task0");
		t.addTaskListener(this);
		t.setStatus(Status.DONE);
		assertTrue(synced); synced = false;
		
		t.setStatus(Status.DONE);
		assertFalse(synced);
		
		t.removeTaskListener(this);
		t.setStatus(Status.NOT_DONE);
		assertFalse(synced); synced = false;
		
	}
	
	@Test
	public void testEqualsAndSync(){
		Task t0 = new Task(0,"Task0");
		Task t1 = new Task(0,"Task0");
		Task t2 = new Task(0,"Task1");
		Task t3 = new Task(1,"Task1");
		assertTrue(t0.equals(t1));
		t1.setStatus(Status.DONE);
		assertTrue(!t0.equals(t1));
		
		assertTrue(!t0.equals(t2));
		t0.sync(t2);
		assertTrue(t0.equals(t2));		

		assertTrue(!t0.equals(t3));
		t0.sync(t3);
		assertTrue(!t0.equals(t3));		
	}
	
	@Override
	public void changed(Task object) {
		synced = true;
	}

}
