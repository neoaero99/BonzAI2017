package util;

/**
 * A simple interface that allows for the clone() method being used by generic
 * type reference variables.
 * 
 * @author Joshua Hooker
 *
 * @param <T> The type of object implementing this interface, ideally
 */
public interface Replicable<T> {
	
	/**
	 * Basically a public, generic version of Object's clone method; create an
	 * identical, independent copy of the Object.
	 * 
	 *  @returning	An independent replica of this object
	 */
	public abstract T clone();
}
