package Castles.util.priorityq;

import java.util.Comparator;

/**
 * A comparator that prioritizes objects with greater values over objects with
 * lesser values.
 * 
 * @author Joshua Hooker
 *
 * @param <T>	The type of object to compare
 */
public class MaxComparator<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		return arg0.compareTo(arg1);
	}
}
