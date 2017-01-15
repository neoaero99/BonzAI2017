package Castles.util.graph;

/**
 * A pair of vertices that define a starting and ending point. Vertices in the
 * pair must contain elements, which extend the Comparable<T> interface, because
 * this class implements the Comparable<T> interface and uses the elements of the
 * vertices as a basis for the comparison.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The elements stored in the vertices
 * @param <W>	The elements stored in the edges associated with the vertices
 */
public class VertexPair<E extends Comparable<E>, W extends Comparable<W>>
		implements Comparable<VertexPair<E, W>> {
	
	private Vertex<E, W> start, end;
	
	public VertexPair(Vertex<E, W> startVertex, Vertex<E, W> endVertex) {
		start = startVertex;
		end = endVertex;
	}
	
	// Getters and setters
	
	public void setStart(Vertex<E, W> newStart) { start = newStart; }
	public Vertex<E, W> getStart() { return start; }

	public void setEnd(Vertex<E, W> newEnd) { end = newEnd; }
	public Vertex<E, W> getEnd() { return end; }
	
	@Override
	public int compareTo(VertexPair<E, W> v) {
		// Prioritize comparison with start vertices
		int startComp = compare(start.getElement(), v.getStart().getElement());
		
		if (startComp == 0) {
			// Only compare end vertices if start vertices are equal
			return compare(end.getElement(), v.getEnd().getElement());
		}
		
		return startComp;
	}
	
	/**
	 * Compare two elements with null value checking. Null values are considered
	 * the least value.
	 * 
	 * @param arg0	Some value
	 * @param arg1	Some value
	 * @return		- integer	-> arg0 < arg1
	 * 				zero		-> arg0 == arg1
	 * 				+ integer	-> arg0 > arg1
	 */
	private static <E extends Comparable<E>> int compare(E arg0, E arg1) {
		
		if (arg0 == null && arg1 == null) {
			return 0;
			
		} else if (arg0 == null) {
			return -1;
			
		} else if (arg1 == null) {
			return 1;
			
		} else {
			return arg0.compareTo(arg1);
		}
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof VertexPair) {
			VertexPair vp = (VertexPair)obj;
			
			if (start == null && vp.start == null && end == null && vp.end == null) {
				// All are null
				return true;
				
			} else if (start == null || vp.start == null || end == null ||
					vp.end == null) {
				// Only one of them are null
				return false;
			}
			
			return (start.equals(vp.start) && end.equals(vp.end)) ||
					(start.equals(vp.end) && end.equals(vp.start));
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("s: %s; e: %s", start, end);
	}
}
