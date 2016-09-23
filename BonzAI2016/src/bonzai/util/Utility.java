package bonzai.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bonzai.Position;
import bonzai.Positionable;

/**
 *
 **/
public class Utility {

	// static class means private constructor
	private Utility() {}

	/**
	 * Returns a collection which represents the intersection of the given Collections
	 *
	 * @param first A base Collection. Must be present
	 * @param rest Any Collections to intersect with the first collection
	 * @return the intersection of the two specified collections
	 **/
	@SafeVarargs
	public static final <E> Set<E> intersect(Collection<E> first, Collection<E>... rest) {
		Set<E> result = new HashSet<>(first);
		for(Collection<E> c : rest) { result.retainAll(c); }
		return result;
	}

	/**
	 * Returns a collection which represents the union of two other collections.
	 *
	 * @param first A base Collection. Must be present
	 * @param rest Any Collections to intersect with the first collection
	 * @return the union of the two specified collections
	 **/
	@SafeVarargs
	public static final <E> Set<E> union(Collection<E> first, Collection<E>... rest) {
		Set<E> result = new HashSet<>(first);
		for(Collection<E> c : rest) { result.addAll(c); }
		return result;
	}

	/**
	 * Returns a collection which represents the set-theoretic difference of two
	 * other collections. This is also known as the complement (of the right
	 * argument, with regards to the left argument).
	 *
	 * @param left
	 * @param right
	 * @return the set-theoretic difference of the two specified collections
	 **/
	public static final <E> Set<E> difference(Collection<E> left, Collection<E> right) {
		Set<E> result = new HashSet<>(left);
		result.removeAll(right);
		return result;
	}

	/**
	 * Chooses a random element from the given collection.
	 *
	 * @param collection the collection from which to choose
	 * @return a randomly chosen element from the specified collection
	 **/
	public static final <E> E any(Collection<E> collection) {
		if(collection.size() == 0) return null;
	
		final int CHOICE = (int)(Math.random() * collection.size());
		
		//return (E)collection.toArray()[CHOICE];				// Calling toArray is potentially expensive
		
		Iterator<E> iter = collection.iterator();
		for(int x = 0; x < CHOICE; x += 1) { iter.next(); }
		return iter.next();
	}

	/**
	 * Extracts the positions of all the positionable objects specified.
	 *
	 * @param positionables an argument list of positionable objects
	 * @return a set of positions corresponding to the positions of all the
	 * positionable objects given
	 **/
	// Possibly change to use Streams
	public static final Set<Position> positions(Positionable... positionables) {
		Set<Position> positions = new HashSet<Position>();
		for(Positionable p : positionables) {
			if(p.getPosition() != null) { positions.add(p.getPosition()); }
		}
		
		// positionables.stream().forEach((p) -> { if (p != null) positions.add(p.position()); });
		return positions;
	}

	/**
	 * Extracts the positions of all the positionable objects specified.
	 *
	 * @param collections an argument list of collections of positionable objects
	 * @return a set of positions corresponding to the positions of all the
	 * positionable objects given
	 **/
	@SafeVarargs
	public static final Set<Position> positions(Collection<? extends Positionable>... collections) {
		Set<Position> positions = new HashSet<Position>();
		for(Collection<? extends Positionable> c : collections) {
			for(Positionable p : c) {
				if(p.getPosition() != null) { positions.add(p.getPosition()); }
			}
		}
		// collections.stream().forEach((c) -> c.stream.forEach((p) -> { if (p.position() != null) positions.add(p.position()) }));
		
		return positions;
	}

	/**
	 * Returns a list containing the elements of the specified collection,
	 * ordered according to the specified comparator.
	 *
	 * @param collection the collection to order
	 * @param comparator the comparator to determine the order of the resulting
	 * list. A null value indicates that the elements' natural ordering should
	 * be used
	 * @return a list ordered according to the comparator, or according to the
	 * natural ordering if no comparator is specified
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable using the specified comparator
	 **/
	public static <E> List<E> order(Collection<E> collection, Comparator<? super E> comparator) {
		List<E> ordered = new ArrayList<>(collection);
		Collections.sort(ordered, comparator);
		return ordered;
	}
	
	/**
	 * Returns a list containing the elements of the specified collection,
	 * ordered according to the natural ordering
	 *
	 * @param collection the collection to order
	 * @return a list ordered according to the natural ordering
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable (for example, strings and integers)
	 **/
	public static <E extends Comparable<E>> List<E> order(Collection<E> collection) {
		return order(collection, null);
	}
	
	/**
	 * Returns the smallest element of the specified collection according to the specified comparator.
	 *
	 * @param collection the collection to inspect
	 * @param comparator the comparator to determine the order. A null value
	 * indicates that the elements' natural ordering should be used
	 * @return the first ordered element according to the comparator, or
	 * according to the natural ordering if no comparator is specified
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable using the specified comparator
	 **/
	@SuppressWarnings("unchecked")
	public static <E> E min(Collection<E> collection, Comparator<? super E> comparator) {
		//*
		E min = null;
		if(collection != null) {
			for(E e : collection) {
				if(min == null || (comparator == null ? ((Comparable<E>)min).compareTo(e) : comparator.compare(min, e)) > 0) {
					min = e;
				}
			}
		}
		return min;
		//*/
		
		//return collection.stream().min(comparator).get();
	}
	
	/**
	 * Returns the smallest element of the specified collection according to the
	 * natural ordering.
	 *
	 * @param collection the collection to inspect
	 * @return the first ordered element according to the natural ordering
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable (for example, strings and integers)
	 **/
	public static <E extends Comparable<E>> E min(Collection<E> collection) {
		return min(collection, null);
	}

	/**
	 * Returns the largest element of the specified collection according to the
	 * specified comparator.
	 *
	 * @param collection the collection to inspect
	 * @param comparator the comparator to determine the order. A null value
	 * indicates that the elements' natural ordering should be used
	 * @return the last ordered element according to the comparator, or
	 * according to the natural ordering if no comparator is specified
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable using the specified comparator
	 **/
	@SuppressWarnings("unchecked")
	public static <E> E max(Collection<E> collection, Comparator<? super E> comparator) {
		
		E max = null;
		if(collection != null) {
			for(E e : collection) {
				if(max == null || (comparator == null ? ((Comparable<E>)max).compareTo(e) : comparator.compare(max, e)) < 0) {
					max = e;
				}
			}
		}
		return max;
		
		//if (collection == null || collection.isEmpty()) return null;
		//return collection.stream().max(comparator).get();						// Default comparator ???
	}
	
	/**
	 * Returns the largest element of the specified collection according to the
	 * natural ordering.
	 *
	 * @param collection the collection to inspect
	 * @return the last ordered element according to the natural ordering
	 *
	 * @throws ClassCastException if the collection contains elements that are
	 * not mutually comparable (for example, strings and integers)
	 **/
	public static <E extends Comparable<E>> E max(Collection<E> collection) {
		return max(collection, null);
	}

	/**
	 * Filters the collection according to the specified predicate, where 
	 * predicate truth indicates that an object is kept. For example,
	 * <pre><code>retain(positions, Occupied)</code></pre>
	 * will return a new set containing only the positions which are occupied.
	 *
	 * @param collection the collection
	 * @param predicate the predicate to apply
	 * @return a set containing only the elements which pass the predicate
	 **/
	public static <E> Set<E> retain(Collection<E> collection, Predicate<? super E> predicate) {
		//return collection.stream().filter((e) -> !predicate.test(e)).collect(Collectors.toCollection(HashSet<E>::new));
		
		Set<E> result = new HashSet<>();
		for(E e : collection) { if(predicate.test(e)) { result.add(e); } }
		return result;
	}

	/**
	 * Filters the list according to the specified predicate, where predicate
	 * truth indicates that an object is removed. For example,
	 * <pre><code>filter(positions, Occupied)</code></pre>
	 * will return a new list containing only the positions which are occupied.
	 * <p>
	 * Applying a filter on a list maintains order.
	 *
	 * @param list the collection
	 * @param predicate the predicate to apply
	 * @return a list containing only the elements which pass the predicate
	 **/
	public static <E> List<E> retain(List<E> list, Predicate<? super E> predicate) {
		//return list.stream().filter((e) -> !predicate.test(e)).collect(Collectors.toCollection(LinkedList<E>::new));
		
		List<E> result = new ArrayList<>();
		for(E e : list) { if(predicate.test(e)) { result.add(e); } }
		return result;
	}

	/**
	 * Filters the collection according to the specified predicate, where 
	 * predicate truth indicates that an object is removed. For example,
	 * <pre><code>filter(positions, Occupied)</code></pre>
	 * will return a new set containing only the positions which are unoccupied.
	 *
	 * @param collection the collection
	 * @param predicate the predicate to apply
	 * @return a set containing only the elements which fail the predicate
	 **/
	public static <E> Set<E> filter(Collection<E> collection, Predicate<? super E> predicate) {
		//return retain(collection, (e) -> !predicate.test(e));
		return retain(collection, new NotPredicate<E>(predicate));
	}

	/**
	 * Filters the list according to the specified predicate, where predicate
	 * truth indicates that an object is removed. For example,
	 * <pre><code>filter(positions, Occupied)</code></pre>
	 * will return a new list containing only the positions which are 
	 * unoccupied. 
	 * <p>
	 * Applying a filter on a list maintains order.
	 *
	 * @param list the collection
	 * @param predicate the predicate to apply
	 * @return a list containing only the elements which fail the predicate
	 **/
	public static <E> List<E> filter(List<E> list, Predicate<? super E> predicate) {
		//return retain(list, (e) -> !predicate.test(e));
		return retain(list, new NotPredicate<E>(predicate));
	}
	
	/**
	 * Finds the given object in the given collection
	 * Useful for updating local entity objects to any changes
	 */
	public static <E> E find(Collection<E> list, E obj) {
		for (E e : list)
			if (obj.equals(e))
				return e;
		
		return null;
	}
	
	
	// Suggest moving these methods to snowbound.api.util.Utility (would inherit from bonzai.uitl.Utility)
	/**
	 * Finds the closest thing in a set to another given object.
	 * For example,
	 * <pre><code>nearest(turn.tiles(), turn.actor());</code></pre>
	 * will return the nearest tile to the current actor.
	 * The set must contain objects that have a position, and the target must
	 * also have a position.
	 * 
	 * @param collection A collection of objects of which to find the nearest
	 * @param target Find the nearest object from the collection relative to this
	 */
	//public static <E extends Positionable> E nearest(Collection<E> collection, Positionable target) {
		//return min(collection, new ManhattanDistance(target));
	//}
	
	/**
	 * Finds the furthest thing in a set to another given object.
	 * For example,
	 * <pre><code>furthest(turn.tiles(), turn.actor());</code></pre>
	 * will return the furthest tile to the current actor.
	 * The set must contain objects that have a position, and the target must
	 * also have a position.
	 * 
	 * @param collection A collection of objects of which to find the furthest
	 * @param target Find the furthest object from the collection relative to this
	 */
	//public static <E extends Positionable> E furthest(Collection<E> collection, Positionable target) {
		//return max(collection, new ManhattanDistance(target));
	//}
}
