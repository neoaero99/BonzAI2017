package bonzai.util;

// e-mail to jason (and utility)
public class NotPredicate<E> implements Predicate<E> {
	Predicate<? super E> pred;
	
	public NotPredicate(Predicate<? super E> pred) {
		this.pred = pred;
	}
	
	public static <E> NotPredicate<E> make(Predicate<? super E> p) {
		return new NotPredicate<>(null);
	}

	@Override
	public boolean test(E e) {
		return !pred.test(e);
	}

}
