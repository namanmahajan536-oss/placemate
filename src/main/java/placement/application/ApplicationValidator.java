package placement.application;

import java.util.*;

/**
 * Validates application data.
 */
public class ApplicationValidator {
    public List<String> validateApplication(Application app) {
        List<String> errors = new ArrayList<>();
        if (app.getStudentId() == null || app.getStudentId().isEmpty()) errors.add("Student ID required");
        if (app.getDriveId() == null || app.getDriveId().isEmpty()) errors.add("Drive ID required");
        if (app.getCompanyId() == null || app.getCompanyId().isEmpty()) errors.add("Company ID required");
        return errors;
    }

    public boolean checkDuplicateApplication(String studentId, String driveId,
                                              List<Application> existing) {
        for (Application a : existing) {
            if (a.getStudentId().equals(studentId) && a.getDriveId().equals(driveId)) return true;
        }
        return false;
    }

    public boolean validateApplicationTiming(String driveLastDate) {
        // Simple check - in production would compare dates
        return driveLastDate != null && !driveLastDate.isEmpty();
    }
}
