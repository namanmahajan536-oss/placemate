package placement.student;

import placement.exception.InvalidInputException;
import java.util.*;

/**
 * Validates student data with static methods.
 */
public class StudentValidator {
    private static final Set<String> VALID_BRANCHES = new HashSet<>(
        Arrays.asList("CSE", "ECE", "ME", "CE", "EE", "IT")
    );

    public static void validateRollNumber(String roll) {
        if (roll == null || roll.trim().isEmpty()) throw new InvalidInputException("Roll number is required.");
        if (roll.length() < 5) throw new InvalidInputException("Roll number must be at least 5 characters.");
    }

    public static void validateCGPA(double cgpa) {
        if (cgpa < 0 || cgpa > 10) throw new InvalidInputException("CGPA must be between 0 and 10.");
    }

    public static void validateBranch(String branch) {
        if (branch == null || !VALID_BRANCHES.contains(branch.toUpperCase()))
            throw new InvalidInputException("Branch must be one of: " + VALID_BRANCHES);
    }

    public static void validateEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new InvalidInputException("Invalid email format.");
    }

    public static void validateName(String name) {
        if (name == null || name.trim().length() < 2)
            throw new InvalidInputException("Name must be at least 2 characters.");
    }

    public static List<String> validateAll(Student student) {
        List<String> errors = new ArrayList<>();
        try { validateName(student.getName()); } catch (InvalidInputException e) { errors.add(e.getMessage()); }
        try { validateRollNumber(student.getRollNumber()); } catch (InvalidInputException e) { errors.add(e.getMessage()); }
        try { validateBranch(student.getBranch()); } catch (InvalidInputException e) { errors.add(e.getMessage()); }
        try { validateCGPA(student.getCgpa()); } catch (InvalidInputException e) { errors.add(e.getMessage()); }
        try { validateEmail(student.getEmail()); } catch (InvalidInputException e) { errors.add(e.getMessage()); }
        return errors;
    }

    public static Set<String> getValidBranches() { return VALID_BRANCHES; }
}
