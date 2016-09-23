package lazers.api;

import bonzai.Team;
import bonzai.util.Predicate;

public class isRotatable implements Predicate<Repeater> {
	private Team me;
	
	public isRotatable(Team team) {
		this.me = team;
	}

	@Override
	public boolean test(Repeater e) {
		return e.getCooldown() == 0 && (e.getControllers().isEmpty() || e.getControllers().contains(me));
	}

}
