package priorityq;

public class MinPQ<K extends Comparable<K>, V> {
	private PQEntry<K, V>[] heap;
	private int size;
	
	@SuppressWarnings("unchecked")
	public MinPQ() {
		heap = (PQEntry<K, V>[]) new PQEntry[1];
		size = 0;
	}
	
	@SuppressWarnings("unchecked")
	public MinPQ(int iniSize) {
		heap = (PQEntry<K, V>[]) new PQEntry[ Math.max(1, iniSize) ];
		size = 0;
	}
	
	public static void main(String[] args) {
		MinPQ<Integer, Boolean> PQ = new MinPQ<Integer, Boolean>();
		PQ.insert(3, true);
		PQ.insert(5, true);
		PQ.insert(1, true);
		PQ.insert(8, true);
		PQ.insert(2, true);
		
		System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
		
		System.out.printf("Min: %s\n", PQ.removeMin());
		
		System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
		
		PQ.replaceKey(2, 0);
		
		System.out.printf("%s\nSize: %d\n", PQ.toString(1), PQ.size());
	}
	
	public void insert(K key, V value) {
		if (size == heap.length) {
			resize();
		}
		
		heap[size] = new PQEntry<K, V>(key, value);
		heap[size].setIndex(size);
		heapUp(size);
		++size;
	}
	
	@SuppressWarnings("unchecked")
	private void resize() {
		int newSize = 2 * heap.length;
		PQEntry<K, V>[] newHeap = (PQEntry<K, V>[]) new PQEntry[newSize];
		
		for (int idx = 0; idx < size; ++ idx) {
			newHeap[idx] = heap[idx];
		}
		
		heap = newHeap;
	}
	
	public K replaceKey(int posIdx, K newKey) throws PQOpException {
		if (posIdx < 0 || posIdx >= size) {
			String msg = String.format("Position %d is out of bounds!", posIdx);
			throw new PQOpException(msg);
		}
		
		K oldKey = heap[posIdx].getKey();
		heap[posIdx].setKey(newKey);
		
		if (posIdx > 0 && heap[posIdx].compareTo(heap[parent(posIdx)]) < 0) {
			heapUp(posIdx);
			
		} else {
			heapDown(posIdx);
		}
		
		return oldKey;
	}
	
	public V min() throws PQOpException {
		if (size == 0) {
			throw new PQOpException("The PQ is empty!");
		}
		
		return heap[0].getValue();
	}
	
	public V removeMin() throws PQOpException {
		if (isEmpty()) {
			throw new PQOpException("The PQ is empty!");
		}
		
		V val = heap[0].getValue();
		invalidate(heap[0]);
		
		heap[0] = heap[--size];
		heap[size] = null;
		heapDown(0);
		
		return val;
	}
	
	/* TODO add remove(K) ? */
	
	private void invalidate(PQEntry<K, V> entry) {
		entry.setKey(null);
		entry.setValue(null);
		entry.setIndex(-1);
	}
	
	private void heapUp(int idx) {
		int parentIdx = parent(idx);
		
		while (idx > 0 && heap[idx].compareTo(heap[parentIdx]) < 0) {
			swapEntries(idx, parentIdx);
			idx = parentIdx;
		}
	}
	
	private void heapDown(int idx) {
		while (isInternal(idx)) {
			int leftIdx = leftChild(idx),
				rightIdx = rightChild(idx);
			
			if (rightIdx < size && heap[rightIdx].compareTo(heap[leftIdx]) < 0
								&& heap[rightIdx].compareTo(heap[idx]) < 0) {
				
				swapEntries(idx, rightIdx);
				idx = rightIdx;
				continue;
				
			} else if (heap[leftIdx].compareTo(heap[idx]) < 0) {
				swapEntries(idx, leftIdx);
				idx = leftIdx;
				
			} else {
				break;
			}
		}
	}
	
	private void swapEntries(int idx1, int idx2) {
		PQEntry<K, V> limbo = heap[idx1];
		limbo.setIndex(idx2);
		heap[idx1] = heap[idx2];
		heap[idx1].setIndex(idx1);
		heap[idx2] = limbo;
	}
	
	private boolean isInternal(int idx) {
		return idx >= 0 && leftChild(idx) < size;
	}
	
	private static int parent(int idx) {
		return (idx - 1) / 2;
	}
	
	private static int leftChild(int idx) {
		return 2 * idx + 1;
	}
	
	private static int rightChild(int idx) {
		return 2 * idx + 2;
	}
	
	/**
	 * @return	Are there any elements in the heap?
	 */
	public boolean isEmpty() { return (size == 0); }
	
	/**
	 * @return	The number of elements in the heap
	 */
	public int size() { return size; }
	
	public void printHeap() {
		printHeap(0, "");
	}
	
	public void printHeap(int idx, String prefix) {
		
		if (idx < 0 || idx >= size) {
			return;
		}
		
		System.out.printf("%s%d: %s\n", prefix, idx, heap[idx]);
		
		printHeap(leftChild(idx), prefix + "  ");
		printHeap(rightChild(idx), prefix + "  ");
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public String toString(int flag) {
		String list = "[ ";
		
		for (int idx = 0; idx < size; ++idx) {
			
			if (flag == 1) {
				list += String.format("%s(%d)", heap[idx], heap[idx].getIndex());
				
			} else if (flag == 2) {
				list += heap[idx].getKey();
				
			} else if (flag == 3) {
				list += heap[idx].getValue();
				
			} else {
				list += heap[idx];
			}
			
			if (idx < (size - 1)) {
				list += ", ";
			}
		}
		
		return (list + " ]");
	}
}
