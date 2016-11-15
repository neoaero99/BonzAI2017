package Castles.util.priorityq;

import java.util.Comparator;

/**
 * A adaptable priority queue (PQ) of key-value pair entries, sorted based on
 * keys of type, K. The backend of the PQ is a heap array. Also, the entries
 * of the PQ are position aware.
 * 
 * @author Joshua Hooker
 *
 * @param <K>	The key type of the PQ entries
 * @param <V>	The value type of the PQ entries
 */
public class AdaptablePQ<K extends Comparable<K>, V> {
	private PQEntry<K, V>[] heap;
	// The size of the PQ (not the heap!)
	private int size;
	// The method used to order the PQ
	private final Comparator<PQEntry<K, V>> mediator;
	
	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public AdaptablePQ() {
		heap = (PQEntry<K, V>[]) new PQEntry[1];
		size = 0;
		mediator = new MaxComparator<PQEntry<K, V>>();
	}
	
	/**
	 * Define a different comparator for the PQ.
	 * 
	 * @param mediator	The method used to order the PQ
	 */
	@SuppressWarnings("unchecked")
	public AdaptablePQ(Comparator<PQEntry<K, V>> mediator) {
		heap = (PQEntry<K, V>[]) new PQEntry[1];
		size = 0;
		this.mediator = mediator;
	}
	
	/**
	 * Defines a starting size for the heap structure of the PQ, which does not
	 * reflect the size of PQ.
	 * 
	 * @param iniSize	The initial size of the heap
	 */
	@SuppressWarnings("unchecked")
	public AdaptablePQ(int iniSize) {
		heap = (PQEntry<K, V>[]) new PQEntry[ Math.max(1, iniSize) ];
		size = 0;
		mediator = new MaxComparator<PQEntry<K, V>>();
	}
	
	/**
	 * Defines a starting size for the heap structure of the PQ, which does not
	 * reflect the size of PQ. Also, defines the mediator for the PQ.
	 * 
	 * @param iniSize	The initial size of the heap
	 * @param mediator	The method used to order the PQ
	 */
	@SuppressWarnings("unchecked")
	public AdaptablePQ(int iniSize, Comparator<PQEntry<K, V>> mediator) {
		heap = (PQEntry<K, V>[]) new PQEntry[ Math.max(1, iniSize) ];
		size = 0;
		this.mediator = mediator;
	}
	
	/**
	 * Testing stuff ...
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		AdaptablePQ<Integer, Boolean> PQ = new AdaptablePQ<Integer, Boolean>(
			new MinComparator<PQEntry<Integer, Boolean>>());
		
		PQ.insert(3, true);
		
		PQEntry<Integer, Boolean> entry = PQ.insert(8, true);
		
		PQ.insert(1, true);
		PQ.insert(5, true);
		PQ.insert(2, true);
		
		System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
		
		//System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
		
		PQ.replaceKey(entry.getIndex(), 7);
		
		System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
		
		PQ.removeMax();
		System.out.printf("%s\n", entry);
	}
	
	/**
	 * Adds an entry for the given key-value pair to the PQ. The entry
	 * associated with the pair is returned, so it may be used to optimize
	 * key replacement, since the entries are position aware.
	 * 
	 * @param key	The key of the new entry
	 * @param value	The value to be associated with the given key
	 * @return		A reference to the new entry
	 */
	public PQEntry<K, V> insert(K key, V value) {
		if (size == heap.length) {
			resize();
		}
		
		heap[size] = new PQEntry<K, V>(key, value);
		// Entry awareness
		heap[size].setIndex(size);
		heapUp(size);
		return heap[size++];
	}
	
	/**
	 * Doubles the heap size and replace the elements of the PQ into the new
	 * heap.
	 */
	@SuppressWarnings("unchecked")
	private void resize() {
		int newSize = 2 * heap.length;
		PQEntry<K, V>[] newHeap = (PQEntry<K, V>[]) new PQEntry[newSize];
		
		for (int idx = 0; idx < size; ++ idx) {
			newHeap[idx] = heap[idx];
		}
		
		heap = newHeap;
	}
	
	/**
	 * Replaces the key of the entry stored at the given index in the heap, and
	 * reorganizes the heap after the replacement.
	 * 
	 * @param posIdx	The position of the entry
	 * @param newKey	The new key for the entry stored at posIdx
	 * @return			The old key value of the entry
	 * @throws			PQOpException- if the given index is out of bounds of
	 * 						the heap
	 */
	public K replaceKey(int posIdx, K newKey) throws PQOpException {
		
		if (posIdx < 0 || posIdx > size) {
			String msg = String.format("Position %d is out of bounds!", posIdx);
			throw new PQOpException(msg);
		}
		
		K oldKey = heap[posIdx].getKey();
		heap[posIdx].setKey(newKey);
		
		// Does the modified child's parent have a greater key?
		if (posIdx > 0 && heap[posIdx].compareTo(heap[parent(posIdx)]) < 0) {
			// If so, then reorganize the heap
			heapUp(posIdx);
			
		} else {
			// Check the modified entry against its children
			heapDown(posIdx);
		}
		
		return oldKey;
	}
	
	/**
	 * Returns the value of entry, whose key is the greatest of all the PQ
	 * entries.
	 * 
	 * @return	The value of the entry with the greatest key value
	 * @throws 	PQOpException- if the PQ is empty
	 */
	public V max() throws PQOpException {
		if (size == 0) {
			throw new PQOpException("The PQ is empty!");
		}
		
		return heap[0].getValue();
	}
	
	/**
	 * Returns the value of the entry, whose key is the greatest of all entries
	 * in the PQ and removes the entry from the PQ.
	 * 
	 * @return	The value of the entry with the greatest key value
	 * @throws 	PQOpException- if the PQ is empty
	 */
	public V removeMax() throws PQOpException {
		if (isEmpty()) {
			throw new PQOpException("The PQ is empty!");
		}
		
		V val = heap[0].getValue();
		swapEntries(0, --size);
		
		// Remove entry reference
		invalidate(heap[size]);
		
		// If necessary, reorganize the heap
		heapDown(0);
		
		return val;
	}
	
	/* TODO add remove(K) ? */
	
	/**
	 * Removes all the values associated with the given entry: key, value and
	 * index.
	 * 
	 * @param entry	A non-null entry to invalidate
	 */
	private void invalidate(PQEntry<K, V> entry) {
		entry.setKey(null);
		entry.setValue(null);
		entry.setIndex(-1);
	}
	
	/**
	 * Push the entry at the given index up the heap, if its key value is less
	 * than that of its parent and ancestors.
	 * 
	 * @param idx	The position of the entry to push up the heap
	 */
	private void heapUp(int idx) {
		int parentIdx = parent(idx);
		
		while (idx > 0 && mediator.compare(heap[idx], heap[parentIdx]) > 0) {
			swapEntries(idx, parentIdx);
			idx = parentIdx;
			parentIdx = parent(idx);
		}
	}
	
	/**
	 * Push the entry down the heap by comparing its key to that of its
	 * children, picking the child with the greatest key to swap positions with
	 * the entry, if that child's key is greater than that of the entry.
	 * Continue this process until both of the entry's children's keys are less
	 * than that of the entry, or the top of the heap is reached.
	 * 
	 * @param idx	The position of the entry to push down the heap
	 */
	private void heapDown(int idx) {
		while (isInternal(idx)) {
			int leftIdx = leftChild(idx),
				rightIdx = rightChild(idx);
			// Is the right child the greatest?
			if (rightIdx < size && mediator.compare(heap[rightIdx], heap[leftIdx]) > 0
								&& mediator.compare(heap[rightIdx], heap[idx]) > 0) {
				
				swapEntries(idx, rightIdx);
				idx = rightIdx;
				continue;
			// How about the left?
			} else if (mediator.compare(heap[leftIdx], heap[idx]) > 0) {
				swapEntries(idx, leftIdx);
				idx = leftIdx;
				
			} else {
				// Both children have greater keys
				break;
			}
		}
	}
	
	/**
	 * Swap the entries at the two given positions, in the heap.
	 * 
	 * @param idx1	The position of a valid entry in the heap
	 * @param idx2	The position of a valid entry in the heap
	 */
	private void swapEntries(int idx1, int idx2) {
		PQEntry<K, V> limbo = heap[idx1];
		// Update entry position awareness
		limbo.setIndex(idx2);
		heap[idx1] = heap[idx2];
		// Update entry position awareness
		heap[idx1].setIndex(idx1);
		heap[idx2] = limbo;
	}
	
	/**
	 * Determine if the given index points to an entry, who has at least one
	 * child with respect to the heap.
	 * 
	 * @param idx	A valid position in the heap
	 * @return		Whether idx points to an internal entry or not
	 */
	private boolean isInternal(int idx) {
		return idx >= 0 && leftChild(idx) < size;
	}
	
	/**
	 * Returns the position of the entry, which acts as the parent of the entry
	 * at postion idx, in the heap, with respect to the heap.
	 * 
	 * @param idx	A valid position in the heap greater than zero
	 * @return		The position of the parent of the entry
	 */
	private static int parent(int idx) {
		return (idx - 1) / 2;
	}
	
	/**
	 * Returns the position of the entry, which acts as the left child of the
	 * entry at position idx, in the heap, with respect to the heap.
	 * 
	 * @param idx	The position of an internal entry in the heap
	 * @return		The position of the entry's left child
	 */
	private static int leftChild(int idx) {
		return 2 * idx + 1;
	}
	
	/**
	 * Returns the position of the entry, which acts as the right child of the
	 * entry at position idx, in the heap, with respect to the heap.
	 * 
	 * @param idx	The position of an internal entry in the heap
	 * @return		The position of the entry's right child
	 */
	private static int rightChild(int idx) {
		return 2 * idx + 2;
	}
	
	/**
	 * @return	Are there any entries in the PQ?
	 */
	public boolean isEmpty() { return (size == 0); }
	
	/**
	 * @return	The number of entries in the PQ
	 */
	public int size() { return size; }
	
	@Override
	public String toString() {
		// Pass the flag for normal entry output
		return toString(0);
	}
	
	/**
	 * Returns the PQ as an array of its elements, whose values are based on
	 * the inputed flag:
	 * 		1	->	Display each entry with its index
	 * 		2	->	Display only the key of each entry
	 * 		3	->	Display only the value of each entry
	 * 			->	Display the whole entry (excluding its index)
	 * 
	 * @param flag	An integer value
	 * @return		A String, which represents the PQ in the form of a array
	 */
	public String toString(int flag) {
		String list = "[ ";
		
		for (int idx = 0; idx < size; ++idx) {
			
			if (flag == 1) {
				// Entry plus index
				list += String.format("%s(%d)", heap[idx], heap[idx].getIndex());
				
			} else if (flag == 2) {
				// Entry key
				list += heap[idx].getKey();
				
			} else if (flag == 3) {
				// Entry value
				list += heap[idx].getValue();
				
			} else {
				// Entry
				list += heap[idx];
			}
			
			if (idx < (size - 1)) {
				list += ", ";
			}
		}
		
		return (list + " ]");
	}
}
