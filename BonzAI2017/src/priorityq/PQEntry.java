package priorityq;

/**
 * A simple key-value pair heap node with an index, which allows the node to
 * be position aware.
 * 
 * @author Josh
 *
 * @param <K>	The type of the key to store in the node
 * @param <V>	The type of value to store in the node
 */
public class PQEntry<K extends Comparable<K>, V> implements
		Comparable<PQEntry<K, V>> {
	
	private K key;
	private V value;
	private int idx;
	
	public PQEntry() {
		key = null;
		value = null;
		idx = -1;
	}
	
	public PQEntry(K k, V v) {
		key = k;
		value = v;
	}
	
	// Getters and setters for key, value, and idx fields
	
	public void setKey(K newKey) { key = newKey; }
	public K getKey() { return key; }
	
	public void setValue(V newValue) { value = newValue; }
	public V getValue() { return value; }
	
	public void setIndex(int i) { idx = i; }
	public int getIndex() { return idx; }

	@Override
	public int compareTo(PQEntry<K, V> node) {
		/* Null values are shoved into the beginning */
		if (node == null || node.key == null) {
			return -1;
			
		} else if (key == null) {
			return 1;	
		}
		
		return key.compareTo(node.key);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof PQEntry) {
			PQEntry node = (PQEntry)obj;
			/* Compare keys */
			if (key != null && node.key != null) {
				return key.equals(node.key);
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		/* key points to value */
		return String.format("{ %s -> %s }", key, value);
	}
}
