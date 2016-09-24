package linkedlist;

/**
 * A generic dual link list node with a comparable element of type E, a
 * previous node, and a next node.
 * 
 * @author Joshua Hooker
 * 
 * @param <E>	the type of element stored in the node, which implements the
 * 				Comparable interface
 */
public class DualLinkNode<E> {
	
	private E element;
	private DualLinkNode<E> previous, next;
	
	public DualLinkNode() {
		element = null;
		
		previous = null;
		next = null;
	}
	
	public DualLinkNode(E e) {
		element = e;

		previous = null;
		next = null;
	}
	
	/**
	 * Compares the given object of type E with this node's element and returns
	 * if the two are equivalent based on the equals() method from the Object
	 * class.
	 * 
	 * @param e	The object to compare to this node's element
	 * @return	If e is equivalent to this node's element
	 */
	public boolean elementEquals(E e) {
		if (element == null || e == null) {
			return element == null && e == null;
		}
		
		return element.equals(e);
	}
	
	// Getter and setter methods
	
	public boolean isEmpty() { return element == null; }
	public void setElement(E e) { element = e; }
	public E getElement() { return element; }
	
	public boolean hasPrevious() { return previous == null; }
	public void setPrevious(DualLinkNode<E> prev) {previous = prev; }
	public DualLinkNode<E> getPrevious() { return previous; }
	
	public boolean hasNext() { return next == null; }
	public void setNext(DualLinkNode<E> next) { this.next = next; }
	public DualLinkNode<E> getNext() { return next; }
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof DualLinkNode) {
			DualLinkNode node = (DualLinkNode)obj;
			// Compare elements
			if ((element == null && node.element == null) ||
					element.equals( node.element )) {
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("[ %s ]", element);
	}
}
