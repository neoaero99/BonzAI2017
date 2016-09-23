package bonzai;

/**
 * An entity is equivalent to itself regardless of the turn. That is, while the
 * state of the same unit may differ from turn to turn, references to the same
 * unit from different turns are equivalent using the equals method. Similarly,
 * an entity's hash code will remain constant across turns.
 **/
public abstract class Entity<E> {
	private static int generator = 0;
	private final int id;

	//
	protected Entity(Entity<E> e) {
		this.id = e.id;
	}

	//	
	protected Entity() {
		this.id = generator++;
	}
	
	/**
	 * Defines entity equality as ID equality (as opposed to state equality).
	 *
	 * @param o the object to compare to
	 * @return true if the objects are ID equal
	 **/
	@Override
	public final boolean equals(Object o) {
		if(!(o instanceof Entity)) { return false; }
		return id == ((Entity)o).id;
	}
	
	/**
	 * Defines entity hash codes based solely on the entity ID.
	 *
	 * @return the entity hashcode
	 **/
	@Override
	public final int hashCode() {
		return this.id;
	}
}
