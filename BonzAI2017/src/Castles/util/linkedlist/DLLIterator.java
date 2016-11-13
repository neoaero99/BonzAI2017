package Castles.util.linkedlist;

import java.util.Iterator;

/**
 * The iterator class for the dualLinkList class.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of element stored in the linked list
 */
public class DLLIterator<E> implements Iterator<E> {
	
	private DualLinkNode<E> previous;
	// The end of the linked list
	private DualLinkNode<E> tail;
	
	/**
	 * Create an iterator object.
	 * 
	 * @param list	The list to iterate over
	 */
	public DLLIterator(DualLinkList<E> list) {
		previous = list.Head;
		tail = list.Tail;
	}
	
	@Override
	public boolean hasNext() {
		return previous.getNext() != tail;
	}

	@Override
	public E next() {
		// Update to the current node and return its value
		previous = previous.getNext();
		return previous.getElement();
	}

}
