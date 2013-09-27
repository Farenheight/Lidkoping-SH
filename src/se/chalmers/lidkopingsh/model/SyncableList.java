package se.chalmers.lidkopingsh.model;

import java.util.List;
/**
 * A List that can be synced
 * @author Robin Gronberg
 *
 * @param <T> The type of objects within the list.
 */
public interface SyncableList<T extends Syncable<? super T>> extends Syncable<List<T>>, List<T> {

}
