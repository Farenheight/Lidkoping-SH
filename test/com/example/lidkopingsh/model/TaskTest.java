package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest implements Listener<Task>{
	private boolean synced = false;
	@Test
	public void testListener() {
		Task t = new Task(0, 1, "Task0");
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
	public void testEquals(){
		// TODO: Add taskId to all Task()
		/*Task t1 = new Task(1, "Polering");
		Task t2 = new Task(1, "Polering");
		assertTrue(t1.equals(t2));
		
		t2.setStatus(Status.DONE);
		assertFalse(t1.equals(t2));*/
	}
	@Test
	public void testSync(){
		/*Task t0 = new Task(0,"Task0");
		Task t1 = new Task(0,"Task0");
		Task t2 = new Task(0,"Task1");
		Task t3 = new Task(1,"Task1");
		assertTrue(t0.equals(t1));
		t1.setStatus(Status.DONE);
		assertFalse(t0.equals(t1));
		
		assertFalse(t0.equals(t2));
		t0.sync(t2);
		assertTrue(t0.equals(t2));		

		assertFalse(t0.equals(t3));
		t0.sync(t3);
		assertFalse(t0.equals(t3));	*/	
	}
	
	@Override
	public void changed(Task object) {
		synced = true;
	}
	
	

}

	


