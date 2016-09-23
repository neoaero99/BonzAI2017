package lazers.api;

import java.util.Collection;

import bonzai.Positionable;
import bonzai.Team;

public interface TurnInterface {
	public Collection<Repeater> getRepeaters();
	public Collection<Target> getTargets();
	public Collection<Wall> getWalls();
	
	public int turnsRemaining();
	public int getScore(Team team);
	
	public Team getMyTeam();
	public Collection<Team> getTeams();
	
	
}
