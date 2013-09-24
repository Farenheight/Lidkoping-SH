package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncableArrayList<T extends Syncable<? super T>> extends
		ArrayList<T> implements SyncableList<T> {
	private static final long serialVersionUID = 8556149278420032244L;

	public SyncableArrayList() {
		super();
	}

	public SyncableArrayList(Collection<? extends T> collection) {
		super(collection != null? collection : new ArrayList<T>());
	}

	@Override
	public boolean sync(List<T> newList) {
		List<T> oldList = new ArrayList<T>(this);
		clear();
		boolean listModified = false, objectModified = false;
		int i = 0;

		for (T newObject : newList) {
			objectModified = false;
			for (T oldObject : oldList) {
				if (oldObject.sync(newObject)) {
					listModified = objectModified = true;
					this.add(i++, oldObject);
					break;
				}
			}
			if(!objectModified){
				this.add(i++, newObject);
			}
		}

		// Removes data that doesn't exist in new list
		List<T> delta = new ArrayList<T>(oldList);
		delta.removeAll(newList);
		removeAll(delta);

		return listModified;
	}
}
