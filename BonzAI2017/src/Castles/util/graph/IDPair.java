package Castles.util.graph;

public class IDPair implements Comparable<IDPair> {
	
	public final String first, second;
	
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