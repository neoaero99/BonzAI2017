package bonzai;

/**
 * What would BonzAI be without the capability shout?
 **/
public class ShoutAction implements Action {
	private String message;
	
	public ShoutAction(String message) {
		message = message.replace("\n", "\\n");
		this.message = message.substring(0, Math.min(message.length(),30));
	}
	
	/**
	 * @return the string message of the shout.
	 **/
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return String.format("SHOUT %s", message);
	}
}
