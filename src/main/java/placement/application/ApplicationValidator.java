package placement.application;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Validates application fields, duplicate submissions, and application deadlines.
 */
public class ApplicationValidator {

    /**
     * Validates required fields on an Application object and returns a list of error messages.
     * An empty list indicates the application is valid.
     */
    public List<String> validateApplication(Application app) {
        List<String> errors = new ArrayList<>();
        if (app == null) {
            errors.add("Application cannot be null");
            return errors;
        }
        if (app.getStudentId() == null || app.getStudentId().trim().isEmpty()) {
            errors.add("Student ID required");
        }
        if (app.getDriveId() == null || app.getDriveId().trim().isEmpty()) {
            errors.add("Drive ID required");
        }
        if (app.getCompanyId() == null || app.getCompanyId().trim().isEmpty()) {
            errors.add("Company ID required");
        }
        return errors;
    }

    /**
     * Checks whether a student has already applied to the given drive in the existing applications list.
     *
     * @return true if a duplicate application exists, false otherwise
     */
    public boolean checkDuplicateApplication(String studentId, String driveId, List<Application> existing) {
        if (studentId == null || driveId == null || existing == null) {
            return false;
        }
        for (Application app : existing) {
            if (app != null && studentId.equals(app.getStudentId()) && driveId.equals(app.getDriveId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that the drive's last application date (yyyy-MM-dd) is present and not in the past.
     *
     * @param driveLastDate deadline date string in ISO format (yyyy-MM-dd)
     * @return true if today is on or before driveLastDate
     */
    public boolean validateApplicationTiming(String driveLastDate) {
        if (driveLastDate == null || driveLastDate.trim().isEmpty()) {
            return false;
        }
        String trimmed = driveLastDate.trim();
        try {
            LocalDate deadline = LocalDate.parse(trimmed.length() >= 10 ? trimmed.substring(0, 10) : trimmed);
            return !LocalDate.now().isAfter(deadline);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
