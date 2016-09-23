package bonzai;

/**
 * The state of a team at a specific turn, including the spawn locations, 
 * color, and score.
 * <p>
 * Instances of the Team class will not be updated as the game progresses. If
 * you have a reference to a team from a previous turn, the current turn can
 * provide you with the most up-to-date version.
 **/
public class Team extends Entity<Team> {
	private final lazers.api.Color color;
	private final int score, uid;

	public Team(lazers.api.Color color, int uid) {
		this.color = color;
		this.uid = uid;
		this.score = 0;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param turn the current turn
	 * @param copy the Team object we would like to copy
	 * @param score the current score that the team has
	 */
	public Team(Team copy, int score) {
		super(copy);
		this.color = copy.getColor();
		this.uid = copy.getID();
		this.score = score;
	}
	
	/**
	 * @return this team's color
	 **/
	public lazers.api.Color getColor() {
		return color;
	}
	
	/**
	 * 
	 * @return Team unique identifier
	 */
	public int getID() {
		return uid;
	}
	
	/**
	 * Returns the score of the team at the current turn
	 *
	 * @return this team's score
	 **/
	public int getScore() {
		return score;
	}
	
	@Override
	public String toString() {
		return String.format("[Team | color = %s, score = %d]", color.toString(), score);
	}
}
