package com.example.lidkopingsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyModel {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyStone> ITEMS = new ArrayList<DummyStone>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyStone> ITEM_MAP = new HashMap<String, DummyStone>();

	static {
		for (int i = 0; i < 20; i++) {
			addItem(new DummyStone(i, "O.R (" + i + ")", "Beskrivning ("
					+ i + ")"));
		}
	}

	private static void addItem(DummyStone item) {
		ITEMS.add(item);
		ITEM_MAP.put("" + item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyStone {
		public int id;
		public String name;
		public String desc;
		public String customerName;
		public List<DummyTask> taskList = new ArrayList<DummyTask>();

		public DummyStone(int id, String name, String desc) {
			this.id = id;
			this.name = name;
			this.desc = desc;
			for(int i = 0; i < 5; i++) {
				boolean status = true;
				if(i%2 == 0) {
					status = false;
				}
				taskList.add(new DummyTask("Task " + (i + 1), status));
			} 
		}
 
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static class DummyTask { 
		public String name;
	    public boolean status;
	    
	    DummyTask(String name, boolean status) {
	    	this.name = name;
	    	this.status = status;
	    }
	}
}





