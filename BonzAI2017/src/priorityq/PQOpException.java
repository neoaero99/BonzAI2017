package priorityq;

/**
 * An exception thrown when an error occurs with the Priority Queue PQ
 * (i.e. trying to remove the minimum value when the PQ is empty).
 * 
 * @author Joshua Hooker
 */
public class PQOpException extends RuntimeException {
	private static final long serialVersionUID = 6512159468689757755L;

	public PQOpException(String msg) {
		super(msg);
	}
}
