package com.example.lidkopingsh.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class StoneDummyContent {

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
			addItem(new DummyStone("" + i, "O.R (" + i + ")", "Beskrivning ("
					+ i + ")"));
		}
	}

	private static void addItem(DummyStone item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyStone {
		public String id;
		public String name;
		public String desc;

		public DummyStone(String id, String name, String desc) {
			this.id = id;
			this.name = name;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
