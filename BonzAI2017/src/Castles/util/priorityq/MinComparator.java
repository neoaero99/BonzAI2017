package Castles.util.priorityq;

import java.util.Comparator;

/**
 * A comparator that prioritizes objects with lesser values over objects with
 * greater values.
 * 
 * @author Joshua Hooker
 *
 * @param <T>	The type of object to compare
 */
public class MinComparator<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		// Return the reverse comparison
		// System.out.printf("%s, %s\n", arg0, arg1);
		return arg1.compareTo(arg0);
	}

}
