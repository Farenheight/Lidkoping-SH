package com.example.lidkopingsh.model;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class SyncableArrayListTest {
	@Test
	public void testSyncList(){
		System.out.println("");		
		//Sync a list of elements, no elements added/removed
		SyncableList<SuperClass> list = new SyncableArrayList<SuperClass>();
		list.add(new SuperClass(2,0));
		list.add(new SuperClass(1,0));
		list.add(new SuperClass(3,0));
		list.add(new SuperClass(0,0));
		list.add(new ExtendedClass(3,0));
		list.add(new ExtendedClass(1,0));
		list.add(new ExtendedClass(5,0));
		System.out.println(list);		
		
		SyncableList<SuperClass> list2 = new SyncableArrayList<SuperClass>();
		list2.add(new SuperClass(0,1));
		list2.add(new SuperClass(1,1));
		list2.add(new SuperClass(2,1));
		list2.add(new SuperClass(4,1));
		list2.add(new SuperClass(3,0));
		list2.add(new ExtendedClass(3,1));
		System.out.println(list2);
		
		list.sync(list2);
		System.out.println(list);		
		assertTrue(list.equals(list2));
		
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
			if(this.id == newData.id){
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
