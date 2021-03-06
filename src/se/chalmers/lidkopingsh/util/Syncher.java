package se.chalmers.lidkopingsh.util;

import java.util.ArrayList;
import java.util.List;

public class Syncher {
	public static <T extends Syncable<? super T>> List<T> syncList(
			List<T> oldList, List<T> newList) {
		List<T> returnedList = new ArrayList<T>();
		boolean objectModified = false;
		int i = 0;
		if (newList != null) {
			for (T newObject : newList) { 
				objectModified = false;
				for (T oldObject : oldList) {
					if (oldObject.sync(newObject)) {
						objectModified = true;
						returnedList.add(i++, oldObject);
						break;
					}
				}
				if (!objectModified) {
					returnedList.add(i++, newObject);
				}
			}
		}

		return returnedList;
	}
}
