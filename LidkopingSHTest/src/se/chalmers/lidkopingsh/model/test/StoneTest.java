package se.chalmers.lidkopingsh.model.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;

public class StoneTest {
	@Test
	public void testEquals() {
		List<Task> tasks1 = new ArrayList<Task>();
		tasks1.add(new Task(new Station(0,"Station" + 0)));
		tasks1.add(new Task(new Station(1,"Station" + 1)));

		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task(new Station(0,"Station" + 0)));
		tasks2.add(new Task(new Station(1,"Station" + 1)));

		List<Task> tasks3 = new ArrayList<Task>();
		tasks3.add(new Task(new Station(0,"Station" + 0)));
		tasks3.add(new Task(new Station(1,"Station" + 1)));

		Stone s1 = new Stone(0, "Red and BLue", "Description", "Fronstwork",
				tasks1, "ST-90", "Side and back", "Textstyle", "Ornament", null);
		Stone s2 = new Stone(0, "Red and BLue", "Description", "Fronstwork",
				tasks2, "ST-90", "Side and back", "Textstyle", "Ornament", null);
		Stone s3 = new Stone(1, "Red and BLue", "Description", "Fronstwork",
				tasks3, "ST-90", "Side and back", "Textstyle", "Ornament", null);
		assertTrue(s1.equals(s2));
		assertTrue(s2.equals(s1));
		assertFalse(s2.equals(s3));
	}
}
