package se.chalmers.lidkopingsh.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/**
 * 
 * @author robin
 *
 * @param <T>
 */
public class SyncableArrayList<T extends Syncable<? super T>> implements SyncableList<T> {
	private static final long serialVersionUID = 8556149278420032244L;
	private List<T> data;

	public SyncableArrayList() {
		super();
	}

	public SyncableArrayList(Collection<? extends T> collection) {
		data = (collection != null? new ArrayList<T>(collection) : new ArrayList<T>());
	}

	@Override
	public boolean sync(List<T> newList) {
		List<T> oldList = new ArrayList<T>(data);
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

	@Override
	public boolean add(T object) {
		return data.add(object);
	}

	@Override
	public void add(int location, T object) {
		
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T get(int location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T remove(int location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T set(int location, T object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<T> subList(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] array) {
		// TODO Auto-generated method stub
		return null;
	}
}
