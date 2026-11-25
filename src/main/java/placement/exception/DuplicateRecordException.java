package placement.exception;

/**
 * Thrown when attempting to create a record (such as a user, job, or application)
 * that already exists with the same unique identifier or username.
 */
public class DuplicateRecordException extends PlacementException {

    /**
     * Constructs a new DuplicateRecordException with the specified detail message.
     *
     * @param message descriptive error message
     */
    public DuplicateRecordException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateRecordException with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   underlying exception that triggered this error
     */
    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
