package lazers.api.util;

import java.util.Comparator;

import bonzai.Team;

public class TeamScore implements Comparator<Team> {
	public int compare(Team team1, Team team2) {
		return team2.getScore() - team1.getScore();
	}
}