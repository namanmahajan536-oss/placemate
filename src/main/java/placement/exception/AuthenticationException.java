package placement.exception;

/**
 * Thrown when authentication or session validation fails (for example,
 * invalid username/password or an expired/unknown session ID).
 */
public class AuthenticationException extends PlacementException {

    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message descriptive error message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     *
     * @param message descriptive error message
     * @param cause   underlying exception that triggered this error
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
