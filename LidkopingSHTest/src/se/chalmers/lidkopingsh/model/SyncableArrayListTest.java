package se.chalmers.lidkopingsh.model;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class SyncableArrayListTest implements Listener<Order>{
	@Test
	public void testSyncList() {
		//Sync a list of elements, no elements added/removed
		SyncableList<SuperClass> list = new SyncableArrayList<SuperClass>();
		list.add(new SuperClass(2,0));
		list.add(new SuperClass(1,0));
		list.add(new SuperClass(3,0));
		list.add(new SuperClass(0,0));
		list.add(new ExtendedClass(3,0));
		list.add(new ExtendedClass(1,0));
		list.add(new ExtendedClass(5,0));
//		System.out.println(list);		
		
		SyncableList<SuperClass> list2 = new SyncableArrayList<SuperClass>();
		list2.add(new SuperClass(0,1));
		list2.add(new SuperClass(1,1));
		list2.add(new SuperClass(2,1));
		list2.add(new SuperClass(4,1));
		list2.add(new SuperClass(3,0));
		list2.add(new ExtendedClass(3,1));
//		System.out.println(list2);
		
		list.sync(list2);
//		System.out.println(list);		
		assertTrue(list.equals(list2));
		
	}
	
	private boolean synced = false;
	@Test
	public void testSyncListeners(){
		Order o0 = new Order(0,"","",0,0,"","","","",0,new Customer("", "","", "", "", 0),null);
		o0.addOrderListener(this);
		Product p0 = new Product(0, "none", "none", "", null);
		o0.addProduct(p0);
		Task t0 = new Task(new Station(0, "Task0"));
		Task t1 = new Task(new Station(1, "Task1"));
		p0.addTask(t0);
		p0.addTask(t1);
		
		Order o1 = new Order(0,"","",0,0,"","","","",0,new Customer("", "","", "", "", 0),null);
		Product p1 = new Product(0, "new", "new", "new", null); //clone
		o1.addProduct(p1);
		Task t2 = new Task(new Station(0, "Task0"));
		Task t3 = new Task(new Station(1, "Task1"));
		p1.addTask(t2);
		p1.addTask(t3);
		
		//Change status on one task and check if listener works
		t2.setStatus(Status.DONE);
		o0.sync(o1);
		assertTrue(synced);
		synced = false;
		
		//Add new task on new Order and sync, then check if listener works
		Task t4 = new Task(new Station(2, "Task2"));
		p1.addTask(t4);
		o0.sync(o1);
		t4.setStatus(Status.DONE);
		assertTrue(synced);
		synced = false;
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add new task on new Product and sync, then check if listener works
		Product p3 = new Product(1,"","","",null);		
		o1.addProduct(p3);
		Task t5 = new Task(new Station(5, "Task5"));
		p3.addTask(t5);
		o1.sync(o0);
		t5.setStatus(Status.DONE);
		assertTrue(synced);
		synced = false;
		
	}
	@Override
	public void changed(Order object) {
		synced = true;
	}
	
	private class SuperClass implements Syncable<SuperClass>{
		int id;
		int data;
		boolean synced = false;
		public SuperClass(int id, int data){
			this.id = id;
			this.data = data;
		}
		@Override
		public boolean sync(SuperClass newData) {
			if(this.id == newData.id && newData.getClass() == getClass()){
				synced = newData.synced;
				this.data = newData.data;
				return true;
			}
			return false;
		}
		@Override
		public String toString() {
			return "[" + id + "," + data + "]";
		}
		@Override
		public boolean equals(Object o) {
			if(this == o){
				return true;
			}else if(o == null || getClass() != o.getClass()){
				return false;
			}else{
				return id == ((SuperClass)o).id && data == ((SuperClass)o).data;
			}
		}
	}
	private class ExtendedClass extends SuperClass{
		public ExtendedClass(int id, int data){
			super(id,data);
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "#" + super.toString();
		}
	}

}
