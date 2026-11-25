package placement.exception;

/**
 * Thrown when reading from or writing to file-based JSON storage fails.
 */
public class StorageException extends PlacementException {

    /**
     * Constructs a new StorageException with the specified detail message.
     *
     * @param message descriptive error message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new StorageException with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   underlying exception that triggered this error
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
