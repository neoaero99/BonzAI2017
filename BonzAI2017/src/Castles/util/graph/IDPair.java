package Castles.util.graph;

/**
 * A set of IDs, which refer to the IDs of two vertices.
 * 
 * @author Joshua Hooker
 */
public class IDPair implements Comparable<IDPair> {
	/*
	 * The ID pertaining to a vertex
	 */
	public final String first, second;
	
	/**
	 * Creates a new ID pair of the given IDs.
	 * 
	 * @param f	The ID of some vertex
	 * @param s	The ID of another vertex
	 */
	public IDPair(String f, String s) {
		first = f;
		second = s;
	}
	
	@Override
	public int compareTo(IDPair arg) {
		/* Compare the first integers first and compare the second integers,
		 * if the first are equal */
		if (arg.first.equals(first)) {
			return arg.second.compareTo(second);
		}
		
		return arg.first.compareTo(first);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDPair) {
			return compareTo((IDPair)obj) == 0;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", first, second);
	}

}