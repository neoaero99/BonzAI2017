package bonzai;

/**
 * What would BonzAI be without the capability shout?
 **/
public class ShoutAction implements Action {
	private String message;
	private Position position;	//Where the shouter was
	
	public ShoutAction(String message) {
		message = message.replace("\n", "\\n");
		message = message.substring(0, Math.min(message.length(),30));
		this.message = message;
	}
	
	/**
	 * @return the string message of the shout.
	 **/
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the location for the shout to originate from.
	 *
	 * @param p the position of the shout
	 */
	public void setLocation(Position p) {
		position = p;
	}
	
	/**
	 * @return the position of where the shout is
	 */
	public Position getLocation() {
		return position;
	}
}
