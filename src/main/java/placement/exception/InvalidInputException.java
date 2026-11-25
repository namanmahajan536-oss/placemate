package placement.exception;

/**
 * Thrown when user-provided input fails validation rules (for example,
 * missing required fields, malformed email, or weak password).
 */
public class InvalidInputException extends PlacementException {

    /**
     * Constructs a new InvalidInputException with the specified detail message.
     *
     * @param message descriptive error message
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidInputException with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   underlying exception that triggered this error
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
