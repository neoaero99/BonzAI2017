package lazers.api;

import bonzai.util.Predicate;

public class isTargetValid implements Predicate<Target> {

	@Override
	public boolean test(Target e) {
		return !e.isHit();
	}

}
