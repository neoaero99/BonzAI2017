package Castles.util.linkedlist;

import java.util.Iterator;
import java.util.Scanner;

/**
 * A generic dual link list with sentinel nodes.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	the type of object to store in the dual link list
 */
public class DualLinkList<E> implements Iterable<E> {
	// Sentinel nodes
	public final DualLinkNode<E> Head, Tail;
	int size;
	
	public DualLinkList() {
		Head = new DualLinkNode<E>();
		Tail = new DualLinkNode<E>();
		size = 0;
		/* Connect sentinel nodes */
		connect(Head, Tail);
	}
	
	/**
	 * Testing stuff ...
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		
		DualLinkList<Character> cList = new DualLinkList<Character>();
		
		System.out.printf("%d\n", cList.size);
		
		cList.addToBack('t');
		cList.addToBack('e');
		
		System.out.printf("%d\n", cList.size);
		
		cList.addToBack('s');
		cList.addToBack('t');
		cList.addToBack('\0');
		cList.addToBack(null);
		
		System.out.printf("%d\n", cList.size);
		
		cList.removeVal('^');
		
		System.out.printf("%d\n", cList.size);
		
		cList.clearList();
		
		System.out.printf("%d\n", cList.size);
		
		DualLinkList<Character> cList2 = new DualLinkList<Character>();
		cList2.addToBack('e');
		DualLinkNode<Character> node1 = cList2.addToBack('\0');
		cList2.addToBack('.');
		DualLinkNode<Character> node2 = cList2.addToBack('4');
		cList2.addToBack('%');
		
		System.out.printf("%s\n", cList2);
		
		cList2.swapElements(node1, node2);
		
		System.out.printf("%s\n", cList2);
		
		DualLinkNode<Character> node3 = cList2.addBehind(node2, '4');
		
		System.out.printf("%s\n", cList2);
		
		cList2.removeRef(node3.getElement());
		
		System.out.printf("%s\n", cList2);
		
		DualLinkList<Boolean> bList = new DualLinkList<Boolean>();
		bList.addToBack(false);
		
		DualLinkNode<Integer> n1 = new DualLinkNode<Integer>(0);
		DualLinkNode<Integer> n2 = new DualLinkNode<Integer>(0);
		
		System.out.printf("%b\n", n1 == n2);
		
		/**Scanner in = new Scanner(System.in);
		String[] input = null;
		
		DualLinkList<Integer> list = new DualLinkList<Integer>();
		
		System.out.printf("?: ");
		while (true) {
			try {
				if (in.hasNext()) {
					input = in.nextLine().split(" ");
					
					if (input.length > 0) {
						if (input[0].equals("esc")) {
							if (input.length == 1) {
								break;
							}
							
						} else if (input[0].equals("pr")) {
							if (input.length == 1) {
								System.out.printf("%s\n", list);
								
							} else if (input.length == 2) {
								if (input[1].equals("-f")) {
									System.out.printf("%s\n", list.reverseListString());
								}
								
							} else if (input.length == 2) {
								if (input[1].equals("-r")) {
									System.out.printf("%s\n", list.reverseListString());
								}
								
							}
							
						} else if (input[0].equals("add")) {
							if (input.length > 2) {
								if (input[1].equals("-b")) {
									// Add each succeeding argument to the back of the list
									for (int idx = 2; idx < input.length; ++idx) {
										int val = Integer.parseInt(input[idx]);
										list.addToBack(val);
									}
									
								} else if (input[1].equals("-f")) {
									// Add each succeeding element to the front of the list
									for (int idx = 2; idx < input.length; ++idx) {
										int val = Integer.parseInt(input[idx]);
										list.addToFront(val);
									}
									
								} else if (input[1].equals("-nb")) {
									int idx = Integer.parseInt(input[2]);
									DualLinkNode<Integer> node = list.atIndex(idx);
									
									if (node != null) {
										// Add each element behind the node at the given index
										for (idx = 3; idx < input.length; ++idx) {
											int val = Integer.parseInt(input[idx]);
											list.addBehind(node, val);
										}
									}
									
								} else if (input[1].equals("-nf")) {
									int idx = Integer.parseInt(input[2]);
									DualLinkNode<Integer> node = list.atIndex(idx);
									
									if (node != null) {
										// Add each element behind the node at the given index
										for (idx = 3; idx < input.length; ++idx) {
											int val = Integer.parseInt(input[idx]);
											list.addInFrontOf(node, val);
										}
									}
									
								} else {
									// Add each element to end of the list
									for (int idx = 1; idx < input.length; ++idx) {
										int val = Integer.parseInt(input[idx]);
										list.addToBack(val);
									}
								}
								
							} else {
								// Add each element to end of the list
								for (int idx = 1; idx < input.length; ++idx) {
									int val = Integer.parseInt(input[idx]);
									list.addToBack(val);
								}
							}
							
						} else if (input[0].equals("find")) {
							
							if (input.length > 1) {
								
								if (input[1].equals("-v")) {
									if (input.length == 3) {
										int val = Integer.parseInt(input[2]);
										boolean found = list.findNextVal(list.Head, val) != null;
										System.out.printf("%d: %b\n", val, found);
									}
									
								} else if (input[1].equals("-r")) {
									if (input.length == 3) {
										Integer val = Integer.parseInt(input[2]);
										DualLinkNode<Integer> start = list.atIndex(val);
										val = start.getElement();
										
										boolean found = list.findNextVal(list.Head, val) != null;
										System.out.printf("%d: %b\n", val, found);
									}
									
								} else {
									if (input.length == 2) {
										int val = Integer.parseInt(input[1]);
										boolean found = list.findNextVal(list.Head, val) != null;
										System.out.printf("%d: %b\n", val, found);
									}
								}
							}
							
						} else if (input[0].equals("rm")) {
							if (input.length > 1) {
								if (input[1].equals("-v")) {
									int val = Integer.parseInt(input[2]);
									boolean removed = list.removeVal(val);
									System.out.printf("%d: %b\n", val, removed);
									
								} else if (input[1].equals("-r")) {
									int val = Integer.parseInt(input[2]);
									DualLinkNode<Integer> start = list.atIndex(val);
									val = start.getElement();
									
									boolean removed = list.removeRef(val);
									System.out.printf("%d: %b\n", val, removed);
									
								} else {
									if (input.length == 2) {
										int val = Integer.parseInt(input[1]);
										boolean removed = list.removeVal(val);
										System.out.printf("%d: %b\n", val, removed);
									}
								}
							}
							
						} else if (input[0].equals("clr")) {
							list.clearList();
						}
						
					}
					
					System.out.printf("\n?: ");
				}
				
			} catch (Exception Ex) {
				Ex.printStackTrace();
				System.out.printf("\n?: ");
			}
		}
		
		in.close();
		/**/
	}
	
	/**
	 * Add a new element to the beginning of the list.
	 * 
	 * @param e the element to add to the list
	 * @return	the node containing the new element
	 */
	public DualLinkNode<E> addToFront(E e) {
		return addBehind(Head, e);
	}
	
	/**
	 * Add the new element to the end of the list.
	 * 
	 * @param e	the element to add to the list
	 * @return	the node containing the new element
	 */
	public DualLinkNode<E> addToBack(E e) {
		return addInFrontOf(Tail, e);
	}
	
	/**
	 * Add the new element after the given node in the list.
	 * 
	 * @param node	An inner node of or the Head of this list
	 * @param e		The new element
	 * @return		The node containing the new element
	 * @throws		InvalidNodeException- if aback is Tail
	 */
	public DualLinkNode<E> addBehind(DualLinkNode<E> node, E e) throws
			InvalidNodeException {
		
		if (node == Tail) {
			throw new InvalidNodeException("Cannot add a node after the Tail!");
		}
		
		DualLinkNode<E> newNode = new DualLinkNode<E>(e);
		DualLinkNode<E> next = node.getNext();
		// connect the new node
		connect(newNode, next);
		connect(node, newNode);
		++size;
		
		return newNode;
	}
	
	/**
	 * Add the new element in front of the given node in the list.
	 * 
	 * @param node	An inner node in or the Tail of this list
	 * @param e		The new element
	 * @return		The node containing the new element
	 * @throws		InvalidNodeException- if aback is Head
	 */
	public DualLinkNode<E> addInFrontOf(DualLinkNode<E> node, E e) throws
			InvalidNodeException {
		
		if (node == Head) {
			throw new InvalidNodeException("Cannot add a node before the Head!");
		}
		
		DualLinkNode<E> newNode = new DualLinkNode<E>(e);
		DualLinkNode<E> previous = node.getPrevious();
		// connect the new node
		connect(previous, newNode);
		connect(newNode, node);
		++size;
		
		return newNode;
	}
	
	/**
	 * Replace the value of the node with the new value.
	 * 
	 * @param node	The inner node, of which to replace the value
	 * @param e		The new value of the node
	 * @return		The old value of the node
	 * @throws		InvalidNodeException- if node is not an inner node
	 */
	public E replaceElement(DualLinkNode<E> node, E newVal) throws
			InvalidNodeException {
		
		if (!isInnerNode(node)) {
			throw new InvalidNodeException("Must be an inner node!");
		}
		
		E oldVal = node.getElement();
		node.setElement(newVal);
		return oldVal;
	}
	
	/**
	 * Swap the values of the two nodes.
	 * 
	 * @param node1 An inner node of this list
	 * @param node2	An inner node of this list
	 * @throws		InvalidNodeException- if either node1 or node2 are not
	 * 					inner nodes
	 */
	public void swapElements(DualLinkNode<E> node1, DualLinkNode<E> node2)
			throws InvalidNodeException {
		
		if (!isInnerNode(node1) || !isInnerNode(node2)) {
			throw new InvalidNodeException("node1 and node2 must be inner nodes!");
		}
		
		E limbo = node1.getElement();
		node1.setElement( node2.getElement() );
		node2.setElement(limbo);
	}
	
	/**
	 * Find the node at the given index with respect to the first element in the
	 * list or null if no such element exists.
	 * 
	 * @param idx	The index of the node, for which to search
	 * @return		The node at the specified index or null if no such node
	 * 				exists
	 */
	public DualLinkNode<E> atIndex(int idx) {
		DualLinkNode<E> limbo = Head.getNext();
		
		while (idx-- > 0 && limbo != Tail) {
			limbo = limbo.getNext();
		}
		
		if (limbo == Tail) {
			return null;
			
		} else {
			return limbo;
		}
	}
	
	/**
	 * Find the next occurance of the target after start in the list based on
	 * the value of target.
	 * 
	 * @param start		The inner node, after which to begin searching
	 * @param target	The object, of whose value to search the list
	 * @return			The first node after start containing a value equal to
	 * 					target or null if no such node exists
	 * @throws			InvalidNodeException- if start is null or a reference
	 * 						to Tail
	 */
	public DualLinkNode<E> findNextVal(DualLinkNode<E> start, E target)
			throws InvalidNodeException {
		
		if (start == null || start == Tail) {
			throw new InvalidNodeException("start cannot be null or a reference to Tail!");
		}
		
		DualLinkNode<E> limbo = start.getNext();
		
		while (limbo != Tail && !limbo.elementEquals(target)) {
			limbo = limbo.getNext();
		}
		
		if (limbo == Tail) {
			// No occurance of target
			return null;
			
		} else {
			return limbo;
		}
	}
	
	/**
	 * Find the next occurance of the target after start in the list based on
	 * the reference of target.
	 * 
	 * @param start		The inner node, after which to begin searching
	 * @param target	A pointer to the address space, of which to search the
	 * 					list for a reference
	 * @return			The first node after start containing a reference to
	 * 					target or null if not such node exists
	 * @throws			InvalidNodeException- if start is null or a reference
	 * 						to Tail
	 */
	public DualLinkNode<E> findNextRef(DualLinkNode<E> start, E target)
			throws InvalidNodeException {
		
		if (start == null || start == Tail) {
			throw new InvalidNodeException("start cannot be null or a reference to Tail!");
		}
		
		DualLinkNode<E> limbo = start.getNext();
		
		while (limbo != Tail && limbo.getElement() != target) {
			limbo = limbo.getNext();
		}
		
		if (limbo == Tail) {
			// No occurance of target
			return null;
			
		} else {
			return limbo;
		}
	}
	
	/**
	 * Find the previous occurance of the target before start in the list based
	 * on the value of target.
	 * 
	 * @param start		The inner node, before which to begin searching
	 * @param target	The object, of whose value to search the list
	 * @return			The first node before start containing a value equal to
	 * 					target or null if no such node exists
	 * @throws			InvalidNodeException- if start is null or a reference
	 * 						to Head
	 */
	public DualLinkNode<E> findPrevVal(DualLinkNode<E> start, E target)
			throws InvalidNodeException {
		
		if (start == null || start == Head) {
			throw new InvalidNodeException("start cannot be null or a reference to Head!");
		}
		
		DualLinkNode<E> limbo = start.getPrevious();
		
		while (limbo != Head && !limbo.elementEquals(target)) {
			limbo = limbo.getPrevious();
		}
		
		if (limbo == Head) {
			// No occurance of target
			return null;
			
		} else {
			return limbo;
		}
	}
	
	/**
	 * Find the previous occurance of the target after start in the list based
	 * on the reference of target.
	 * 
	 * @param start		The inner node, before which to begin searching
	 * @param target	A pointer to the address space, of which to search the
	 * 					list for a reference
	 * @return			The first node before start containing a reference to
	 * 					target or null if not such node exists
	 * @throws			InvalidNodeException- if start is null or a reference
	 * 						to Head
	 */
	public DualLinkNode<E> findPrevRef(DualLinkNode<E> start, E target)
			throws InvalidNodeException {
		
		if (start == null || start == Head) {
			throw new InvalidNodeException("start cannot be null or a reference to Head!");
		}
		
		DualLinkNode<E> limbo = start.getPrevious();
		
		while (limbo != Head && limbo.getElement() != target) {
			limbo = limbo.getPrevious();
		}
		
		if (limbo == Head) {
			// No occurance of target
			return null;
			
		} else {
			return limbo;
		}
	}
	
	/**
	 * Removes the first occurance of a reference to target's address space,
	 * in the list.
	 * 
	 * @param target	A pointer to the address space, of which to search the
	 * 					list for a reference
	 * @return			If a node containing a reference to target's address
	 * 					space was successfully removed
	 */
	public boolean removeRef(E target) {
		return rmNextRef(Head, target);
	}
	
	/**
	 * Removes the first node, whose elemnt's value is equivalent to target.
	 * 
	 * @param target	The object, for whose value to search the list
	 * @return			If a node, whose value is equal to target, is
	 * 					successfully removed from the list
	 */
	public boolean removeVal(E target) {
		return rmNextVal(Head, target);
	}
	
	/**
	 * Removes the next occurance of a reference to target's address space
	 * after node, in the list, if one exists.
	 * 
	 * @param node		The node, after which to begin searching
	 * @param target	A pointer to the address space, of which to search the
	 * 					list for a reference
	 * @return			If a node containing a reference to target's address
	 * 					space was successfully removed
	 * @throws			InvalidNodeException- if node is null or a reference to
	 * 						Tail
	 */
	public boolean rmNextRef(DualLinkNode<E> node, E target) throws
			InvalidNodeException {
		
		if (node == null || node == Tail) {
			throw new InvalidNodeException("node cannot null or a reference to Tail!");
		}
		
		DualLinkNode<E> toRemove = findNextRef(node, target);
		
		if (toRemove != null) {
			// Remove the node if it exists
			removeNode(toRemove);
		}
		
		return (toRemove != null);
	}
	
	/**
	 * Removes the next node, which contains a element, whose value is
	 * equivalent to target.
	 * 
	 * @param node
	 * @param target	The object, for whose value to search the list
	 * @return			If a node containing an element, which is equal to
	 * 					target, was successfully removed from the list
	 * @throws			InvalidNodeException- if the given node is null or a
	 * 						reference to Tail
	 */
	public boolean rmNextVal(DualLinkNode<E> node, E target) throws
			InvalidNodeException {
		
		if (node == null || node == Tail) {
			throw new InvalidNodeException("node cannot be null or a reference to Tail!");
		}
		
		DualLinkNode<E> toRemove = findNextVal(node, target);
		
		if (toRemove != null) {
			removeNode(toRemove);
		}
		
		return (toRemove != null);
	}
	
	/**
	 * Removes the give node from the list, invalidates it, and returns its
	 * stored value.
	 * 
	 * @param node	An inner node in this list
	 * @return		The value of the node
	 * @throws		InvalidNodeException- if the given node is not an inner
	 * 					node
	 */
	private E removeNode(DualLinkNode<E> node) throws InvalidNodeException {
		if (!isInnerNode(node)) {
			throw new InvalidNodeException("Must be an inner node!");
		}
		
		connect(node.getPrevious(), node.getNext());
		E element = node.getElement();
		invalidate(node);
		--size;
		
		return element;
	}
	
	/**
	 * Connects the two given nodes references, so that fNode references bNode
	 * as its next node and vice versa.
	 * 
	 * @param fNode	The node in front of bNode
	 * @param bNode	The node behind fNode
	 */
	private void connect(DualLinkNode<E> fNode, DualLinkNode<E> bNode) {
		if (fNode != null) {
			fNode.setNext(bNode);
		}
		
		if (bNode != null) {
			bNode.setPrevious(fNode);
		}
	}
	
	/**
	 * Determines if the given node is a non-null, non-sentinel node.
	 * 
	 * @param node	Some node object
	 * @return		If the node is non-null and not a sentinel
	 */
	public boolean isInnerNode(DualLinkNode<E> node) {
		return node != null && node != Head && node != Tail;
	}
	
	/**
	 * Removes all inner nodes from the list and invalidates each one.
	 */
	public void clearList() {
		DualLinkNode<E> limbo = Head.getNext();
		// Remove all references to any inner nodes
		while (limbo != Tail) {
			limbo = limbo.getNext();
			invalidate( limbo.getPrevious() );
		}
		
		Head.setPrevious(null);
		connect(Head, Tail);
		Tail.setNext(null);
		size = 0;
	}
	
	/***
	 * Remove the references and value of the given inner node.
	 * 
	 * @param node	an inner node in the list
	 * @throws		InvalidNodeException- if the give node is not an inner
	 * 					node
	 */
	private void invalidate(DualLinkNode<E> node) throws InvalidNodeException {
		if (!isInnerNode(node)) {
			throw new InvalidNodeException("Cannot invalidate Head or Tail!");
		}
		
		node.setElement(null);
		node.setPrevious(null);
		node.setNext(null);
	}
	
	/**
	 * @return	the number of inner nodes in the list.
	 */
	public int size() { return size; }
	
	@Override
	public Iterator<E> iterator() {
		return new DLLIterator<E>(this);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof DualLinkList) {
			DualLinkList list = (DualLinkList)obj;
			DualLinkNode lNode = list.Head.getNext();
			DualLinkNode<E> tNode = Head.getNext();
			
			// Check each corresponding pair of elements in the given list and this
			while (lNode != list.Tail && tNode != Tail && tNode.equals(lNode)) {
				lNode = lNode.getNext();
				tNode = tNode.getNext();
			}
			
			return (lNode == list.Tail && tNode == Tail);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String DLList = "{ ";
		
		DualLinkNode<E> limbo = Head.getNext();
		
		while (limbo != Tail) {
			DLList += limbo.getElement();
			
			if (limbo.getNext() != Tail) {
				DLList += ", ";
			}
			
			limbo = limbo.getNext();
		}
		
		return DLList + " }";
	}
	
	/**
	 * Build a string, which represents the list, including the sentinel nodes.
	 * 
	 * @return	A string representation of the of the list
	 */
	public String forwardListString() {
		String DLList = "{ ";
		
		DualLinkNode<E> limbo = Head;
		
		while (limbo != null) {
			DLList += limbo;
			
			if (limbo != Tail) {
				DLList += " <-> ";
			}
			
			limbo = limbo.getNext();
		}
		
		return DLList + " }";
	}
	
	/**
	 * Build a string, which represents the list in reverse, including sentinel
	 * nodes.
	 * 
	 * @return	A string representation of the reverse of the list
	 */
	public String reverseListString() {
		String RevDLList = "{ ";
		DualLinkNode<E> limbo = Tail;
		
		while (limbo != null) {
			RevDLList += limbo;
			
			if (limbo != Head) {
				RevDLList += " <-> ";
			}
			
			limbo = limbo.getPrevious();
		}
		
		return RevDLList + " }";
	}
}
