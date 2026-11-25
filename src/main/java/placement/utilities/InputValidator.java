package placement.utilities;

import placement.exception.InvalidInputException;

/**
 * Terminal input validation utilities.
 */
public class InputValidator {
    public static int validateMenuChoice(String input, int min, int max) {
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice < min || choice > max) throw new InvalidInputException("Choose between " + min + " and " + max);
            return choice;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid number.");
        }
    }

    public static int validateInteger(String input) {
        try { return Integer.parseInt(input.trim()); }
        catch (NumberFormatException e) { throw new InvalidInputException("Please enter a valid integer."); }
    }

    public static double validateDouble(String input, double min, double max) {
        try {
            double val = Double.parseDouble(input.trim());
            if (val < min || val > max) throw new InvalidInputException("Value must be between " + min + " and " + max);
            return val;
        } catch (NumberFormatException e) { throw new InvalidInputException("Please enter a valid number."); }
    }

    public static String validateRequired(String input, String fieldName) {
        if (input == null || input.trim().isEmpty())
            throw new InvalidInputException(fieldName + " is required.");
        return input.trim();
    }

    public static String validateEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new InvalidInputException("Invalid email format.");
        return email.trim();
    }

    public static String validateDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}"))
            throw new InvalidInputException("Date must be in YYYY-MM-DD format.");
        return date.trim();
    }
}
