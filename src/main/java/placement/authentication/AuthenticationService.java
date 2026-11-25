package placement.authentication;

import placement.exception.AuthenticationException;
import placement.exception.InvalidInputException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages user login, logout, and active session tracking using an in-memory HashMap.
 */
public class AuthenticationService {

    private final HashMap<String, User> activeSessions;

    /**
     * Initializes an AuthenticationService with an empty session store.
     */
    public AuthenticationService() {
        this.activeSessions = new HashMap<String, User>();
    }

    /**
     * Authenticates a user by username and raw password against the provided user list.
     * On success, creates a new session and returns the generated session ID.
     *
     * @param username login username
     * @param password plain-text password
     * @param allUsers list of all registered users to search
     * @return newly created session ID
     * @throws InvalidInputException   if username or password is blank
     * @throws AuthenticationException if the user is not found or password is incorrect
     */
    public String login(String username, String password, List<User> allUsers) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }
        if (allUsers == null || allUsers.isEmpty()) {
            throw new AuthenticationException("No registered users available for authentication.");
        }

        User matchedUser = findUserByUsername(username.trim(), allUsers);
        if (matchedUser == null) {
            throw new AuthenticationException("Invalid username or password.");
        }

        if (!PasswordManager.verifyPassword(password, matchedUser.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        String sessionId = generateSessionId();
        activeSessions.put(sessionId, matchedUser);
        return sessionId;
    }

    /**
     * Terminates an active session by removing it from the session map.
     *
     * @param sessionId session identifier to invalidate
     * @return {@code true} if an active session was removed, {@code false} otherwise
     */
    public boolean logout(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        return activeSessions.remove(sessionId.trim()) != null;
    }

    /**
     * Validates a session ID and returns the associated {@link User}.
     *
     * @param sessionId session identifier to validate
     * @return authenticated {@link User} if the session is active, or {@code null} if invalid
     */
    public User validateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return null;
        }
        return activeSessions.get(sessionId.trim());
    }

    /**
     * Checks whether a session ID currently maps to an active user session.
     *
     * @param sessionId session identifier to check
     * @return {@code true} if the session is active
     */
    public boolean isSessionActive(String sessionId) {
        return validateSession(sessionId) != null;
    }

    /**
     * Generates a unique, unpredictable session identifier.
     *
     * @return new session ID string
     */
    public String generateSessionId() {
        return "SESS-" + UUID.randomUUID().toString();
    }

    /**
     * Returns the number of currently active sessions.
     *
     * @return active session count
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * Clears all active sessions (useful during system shutdown or testing).
     */
    public void clearAllSessions() {
        activeSessions.clear();
    }

    /**
     * Returns a shallow copy of currently active sessions mapped by session ID.
     *
     * @return copy of active session map
     */
    public Map<String, User> getActiveSessions() {
        return new HashMap<String, User>(activeSessions);
    }

    private User findUserByUsername(String username, List<User> allUsers) {
        for (User user : allUsers) {
            if (user != null && user.getUsername() != null
                    && user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
}
