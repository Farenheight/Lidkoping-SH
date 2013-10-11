package se.chalmers.lidkopingsh.util;

import java.util.ArrayList;
import java.util.List;

public class Syncher {
	public static <T extends Syncable<? super T>> List<T> syncList(
			List<T> oldList, List<T> newList) {
		List<T> returnedList = new ArrayList<T>();
		boolean listModified = false, objectModified = false;
		int i = 0;
		if (newList != null) {
			for (T newObject : newList) { // TODO: null här, follow the
											// debugger!
				objectModified = false;
				for (T oldObject : oldList) {
					if (oldObject.sync(newObject)) {
						listModified = objectModified = true;
						returnedList.add(i++, oldObject);
						break;
					}
				}
				if (!objectModified) {
					returnedList.add(i++, newObject);
				}
			}
		}
		else{
			returnedList = null;
		}

		// Removes data that doesn't exist in new list
		// List<T> delta = new ArrayList<T>(oldList);
		// delta.removeAll(newList);
		// returnedList.removeAll(delta);
		return returnedList;
	}
}
