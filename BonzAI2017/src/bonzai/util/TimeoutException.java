package bonzai.util;

/**
 * Exception thrown when execution has not completed within a specified
 * timeframe.
 * <p>
 * Not to be confused with java.util.concurrent.TimeoutException and is provided
 * here for convienence.
 **/
public class TimeoutException extends Exception {
	private static final long serialVersionUID = -8709687299308864546L;

	/**
	 * Constructs a TimeoutException with no specified detail message.
	 **/
	public TimeoutException() {
	}

	/**
	 * Constructs a TimeoutException with the specified detail message.
	 *
	 * @param message
	 *            the detail message
	 **/
	public TimeoutException(String message) {
		super(message);
	}
}