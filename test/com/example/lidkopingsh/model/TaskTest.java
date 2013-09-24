package com.example.lidkopingsh.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest implements Listener<Task>{
	private boolean synced = false;
	@Test
	public void test() {
		Task t = new Task(0, "Task0");
		t.addTaskListener(this);
		t.setStatus(Status.DONE);
	}

	@Override
	public void changed(Task object) {
		// TODO Auto-generated method stub
		
	}

}
