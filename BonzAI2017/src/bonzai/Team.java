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
	private final Castles.api.Color color;
	private final int score, uid;
	private String name;

	public Team(Castles.api.Color color, int uid) {
		this.color = color;
		this.uid = uid;
		this.score = 0;
	}
	
	/**This does nothing for players
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param turn - the current turn
	 * @param copy - the Team object we would like to copy
	 * @param score - the new score that the Team object should be assigned
	 */
	public Team(Team copy, int score) {
		super(copy);
		this.color = copy.getColor();
		this.uid = copy.getID();
		this.score = score;
		this.name = copy.name;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the Color of this team.
	 * 
	 * @return this team's color
	 **/
	public Castles.api.Color getColor() {
		return color;
	}
	
	/**
	 * Returns the ID of this Team. This ID remains consistent
	 * across different copies of the same Team object, and 
	 * can be passed to a turnObject.getUtil().updateEntity() call
	 * to retrieve the copy of the Team object on that turn.
	 * 
	 * @return this team's unique identifier
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
	
	/**
	 * Determines if the given team is equivalent to this.
	 * 
	 * @param t
	 * @return
	 */
	public boolean equals(Team t) {
		return color.equals(t.getColor());
	}
	
	/**
	 * Returns a String representation of the Team object,
	 * containing this team's color and score.
	 * 
	 * @return a String containing the team's color and score
	 */
	@Override
	public String toString() {
		return String.format("[Team %s has score = %d]", color.toString(), score);
	}
}
