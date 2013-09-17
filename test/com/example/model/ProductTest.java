package com.example.model;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class ProductTest extends TestCase {
	@Test
	public void testAddTask() {
		// Add task 0 to 9 to product and set status to done if tasknumber is
		// even. Every task is added in first place
		
		/* Name		Status
		 * 
		 * task9 	NOT_DONE
		 * task8 	DONE
		 * task7 	NOT_DONE
		 * task6 	DONE
		 * task5 	NOT_DONE
		 * task4 	DONE
		 * task3 	NOT_DONE
		 * task2 	DONE
		 * task1 	NOT_DONE
		 * task0 	DONE
		 */
		Product p = new Product();
		Task[] tasks = new Task[10];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = new Task("Task" + i, i % 2 == 0? Status.DONE:Status.NOT_DONE);
			p.addTask(tasks[i], 0);
		}
		{
			int i = tasks.length;
			for(Task task: p.getTasks()){
				assertEquals(true, (task.equals(new Task("Task" + --i,i % 2 == 0? Status.DONE:Status.NOT_DONE))));
			}			
		}
		/* Name		Status
		 * 
		 * task0 	DONE
		 * task1 	NOT_DONE
		 * task2 	DONE
		 * task3 	NOT_DONE
		 * task4 	DONE
		 * task5 	NOT_DONE
		 * task6 	DONE
		 * task7 	NOT_DONE
		 * task8 	DONE
		 * task9 	NOT_DONE
		 */
		p = new Product();
		tasks = new Task[10];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = new Task("Task" + i, i % 2 == 0? Status.DONE:Status.NOT_DONE);
			p.addTask(tasks[i], -1);
		}
		{
			int i = 0;
			for(Task task: p.getTasks()){
				assertEquals(true, (task.equals(new Task("Task" + i,i % 2 == 0? Status.DONE:Status.NOT_DONE))));
				i++;
			}			
		}
	}
}
