package placement.authentication;

import placement.exception.InvalidInputException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class providing static password hashing, verification,
 * strength validation, and temporary password generation.
 */
public final class PasswordManager {

    private static final String SALT = "PLCMT$2026#SALT";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIALS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordManager() {
        // Utility class; prevent instantiation.
    }

    /**
     * Hashes a raw password by combining it with a salt string and computing
     * forward and reversed {@link String#hashCode()} values.
     *
     * @param rawPassword plain-text password
     * @return salted hash string
     * @throws InvalidInputException if the password is null or empty
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new InvalidInputException("Password cannot be null or empty.");
        }

        String salted = SALT + rawPassword + SALT;
        int forwardHash = salted.hashCode();
        int reverseHash = new StringBuilder(salted).reverse().toString().hashCode();
        return "HASH_" + Integer.toHexString(forwardHash) + "_" + Integer.toHexString(reverseHash);
    }

    /**
     * Verifies whether a raw password matches a previously generated hash.
     *
     * @param rawPassword  plain-text password to verify
     * @param expectedHash stored password hash
     * @return {@code true} if the password matches the hash, {@code false} otherwise
     */
    public static boolean verifyPassword(String rawPassword, String expectedHash) {
        if (rawPassword == null || rawPassword.isEmpty() || expectedHash == null || expectedHash.isEmpty()) {
            return false;
        }
        String computedHash = hashPassword(rawPassword);
        return computedHash.equals(expectedHash);
    }

    /**
     * Validates password strength and returns a list of descriptive issues.
     * An empty list means the password satisfies all security requirements.
     *
     * @param password plain-text password to evaluate
     * @return list of validation error messages (empty when valid)
     */
    public static List<String> validatePasswordStrength(String password) {
        List<String> issues = new ArrayList<String>();

        if (password == null || password.isEmpty()) {
            issues.add("Password cannot be empty.");
            return issues;
        }

        if (password.length() < 8) {
            issues.add("Password must be at least 8 characters long.");
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasWhitespace = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (Character.isWhitespace(ch)) {
                hasWhitespace = true;
            } else {
                hasSpecial = true;
            }
        }

        if (!hasUpper) {
            issues.add("Password must contain at least one uppercase letter.");
        }
        if (!hasLower) {
            issues.add("Password must contain at least one lowercase letter.");
        }
        if (!hasDigit) {
            issues.add("Password must contain at least one digit.");
        }
        if (!hasSpecial) {
            issues.add("Password must contain at least one special character.");
        }
        if (hasWhitespace) {
            issues.add("Password must not contain spaces.");
        }

        return issues;
    }

    /**
     * Generates a random 10-character temporary password that satisfies all
     * password strength rules (uppercase, lowercase, digit, and special character).
     *
     * @return randomly generated temporary password
     */
    public static String generateTemporaryPassword() {
        List<Character> characters = new ArrayList<Character>();
        characters.add(randomChar(UPPERCASE));
        characters.add(randomChar(LOWERCASE));
        characters.add(randomChar(DIGITS));
        characters.add(randomChar(SPECIALS));

        for (int i = 4; i < 10; i++) {
            characters.add(randomChar(ALL_CHARS));
        }

        Collections.shuffle(characters, RANDOM);

        StringBuilder builder = new StringBuilder(characters.size());
        for (Character ch : characters) {
            builder.append(ch);
        }
        return builder.toString();
    }

    private static char randomChar(String source) {
        return source.charAt(RANDOM.nextInt(source.length()));
    }
}
