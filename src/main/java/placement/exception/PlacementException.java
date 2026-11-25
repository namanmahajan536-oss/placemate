package placement.exception;

/**
 * Base runtime exception for all errors in the Placement Management System.
 * All custom exceptions in the application extend this class.
 */
public class PlacementException extends RuntimeException {

    /**
     * Constructs a new PlacementException with the specified detail message.
     *
     * @param message descriptive error message
     */
    public PlacementException(String message) {
        super(message);
    }

    /**
     * Constructs a new PlacementException with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   underlying exception that triggered this error
     */
    public PlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}
