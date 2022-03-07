// AssociationTable.java
// Association table implements an associatively sorted table containing sets of keys and values
import java.util.TreeMap;
import java.util.Iterator;

public class AssociationTable<K extends Comparable<K>, V> {
	
	private TreeMap<K,V> treeMap;
	
	// empty constructor
	public AssociationTable() {treeMap = new TreeMap<K,V>();}
	
	// constructor with an array of comparable keys and an array of values
	public AssociationTable(K[] keys, V[] values) throws IllegalArgumentException {
		this();
		if (keys.length != values.length) {
			// arrays of keys and values do not match
			throw new IllegalArgumentException("The number of Keys and Values does not match");
		}
		else {
			for (int i = 0; i < keys.length; i++) {
				// for each entry in the array of keys - place it and the corresponding value entry
				// in the tree map, sorted by the natural order of the keys
				// a repeating key will overwrite the previous entry
				treeMap.put(keys[i], values[i]);
			}
		}
	}
	
	// add a new entry of a key and a corresponding value to the table
	// delegating the work to the method put of class TreeMap
	public void add(K key, V value) {treeMap.put(key, value);}
	
	// get the value currently associated with a given key. return null if key is not in the table
	// delegating work to the method get of class TreeMap
	public V get(K key) {return treeMap.get(key);}
	
	// determine if the association table contains a given key, using method containsKey of class TreeMap
	public boolean contains(K key) {return treeMap.containsKey(key);}
	
	// remove the entry associated with a given key from the table
	// returns a boolean indicating success or failure
	public boolean remove(K key) {
		if (contains(key)) {
			// the key exists in the table (may also hold value null)
			// remove the key
			treeMap.remove(key);
			// ensure deletion and return indication of success
			return !contains(key);
		} // key is not in the table
		else {return false;}
	}
	
	// return the number of entries currently in the association table
	public int size() {return treeMap.size();}
	
	// return an iterator of the keys in the table
	public Iterator<K> keyIterator() {return treeMap.navigableKeySet().iterator();}
}
