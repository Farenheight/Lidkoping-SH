package se.chalmers.lidkopingsh.util;

import java.util.List;
/**
 * A List that can be synced. Syncing a SyncableList sync's every element in the list.
 * @author Robin Gronberg
 *
 * @param <T> The type of objects within the list.
 */
public interface SyncableList<T extends Syncable<? super T>> extends Syncable<List<T>>, List<T> {

}
