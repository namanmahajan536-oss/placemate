package placement.application;

import placement.company.PlacementDrive;
import placement.company.PlacementDriveManager;
import placement.eligibility.EligibilityChecker;
import placement.eligibility.EligibilityResult;
import placement.exception.DuplicateRecordException;
import placement.exception.InvalidInputException;
import placement.student.Student;
import placement.student.StudentManager;
import java.util.*;

/**
 * Service layer orchestrating placement application business rules:
 * validation, eligibility checking, duplicate prevention, submission, and tracking.
 */
public class ApplicationService {
    private final ApplicationManager applicationManager;
    private final StudentManager studentManager;
    private final PlacementDriveManager driveManager;
    private final ApplicationValidator validator;
    private final EligibilityChecker eligibilityChecker;
    private int nextAppId = 1;

    public ApplicationService(ApplicationManager applicationManager,
                              StudentManager studentManager,
                              PlacementDriveManager driveManager) {
        this.applicationManager = applicationManager;
        this.studentManager = studentManager;
        this.driveManager = driveManager;
        this.validator = new ApplicationValidator();
        this.eligibilityChecker = new EligibilityChecker();
    }

    /**
     * Submits a student application for a drive, resolving the companyId from the PlacementDrive.
     */
    public Application submitApplication(String studentId, String driveId) {
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;
        if (drive == null) {
            throw new InvalidInputException("Drive not found.");
        }
        return submitApplication(studentId, driveId, drive.getCompanyId());
    }

    /**
     * Submits an application after checking duplicate status, student eligibility, and field validation.
     *
     * @param studentId ID of the applying student
     * @param driveId   ID of the target placement drive
     * @param companyId ID of the organizing company
     * @return the newly created and persisted Application
     */
    public Application submitApplication(String studentId, String driveId, String companyId) {
        Student student = studentManager != null ? studentManager.getStudentById(studentId) : null;
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;
        if (student == null) {
            throw new InvalidInputException("Student not found.");
        }
        if (drive == null) {
            throw new InvalidInputException("Drive not found.");
        }

        // Prevent duplicate applications
        if (applicationManager.isDuplicate(studentId, driveId)) {
            throw new DuplicateRecordException("You have already applied to this drive.");
        }

        // Check eligibility before allowing application submission
        EligibilityResult eligibility = eligibilityChecker.checkEligibility(student, drive);
        if (!eligibility.isEligible()) {
            throw new InvalidInputException("Not eligible: " + String.join(", ", eligibility.getReasons()));
        }

        String resolvedCompanyId = (companyId != null && !companyId.trim().isEmpty())
                ? companyId
                : drive.getCompanyId();

        String appId = generateNextApplicationId();
        Application app = new Application(appId, studentId, driveId, resolvedCompanyId, drive.getCompanyName());
        app.setStudentName(student.getName());
        app.setRollNumber(student.getRollNumber());

        List<String> validationErrors = validator.validateApplication(app);
        if (!validationErrors.isEmpty()) {
            throw new InvalidInputException("Invalid application: " + String.join(", ", validationErrors));
        }

        boolean added = applicationManager.submitApplication(app);
        if (!added) {
            throw new DuplicateRecordException("Duplicate application detected.");
        }
        return app;
    }

    /**
     * Generates a unique sequential application ID (e.g., APP0001), advancing past loaded IDs.
     */
    private synchronized String generateNextApplicationId() {
        for (Application existing : applicationManager.getAllApplications()) {
            String id = existing.getId();
            if (id != null && id.startsWith("APP")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num >= nextAppId) {
                        nextAppId = num + 1;
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric custom IDs
                }
            }
        }
        return "APP" + String.format("%04d", nextAppId++);
    }

    /**
     * Returns all applications for a student.
     */
    public List<Application> trackApplications(String studentId) {
        return applicationManager.getByStudent(studentId);
    }

    /**
     * Returns a student's application history sorted from most recent to oldest.
     */
    public List<Application> getApplicationHistory(String studentId) {
        List<Application> history = applicationManager.getByStudent(studentId);
        Collections.sort(history, (a, b) -> {
            String da = a.getAppliedAt() != null ? a.getAppliedAt() : "";
            String db = b.getAppliedAt() != null ? b.getAppliedAt() : "";
            return db.compareTo(da);
        });
        return history;
    }

    /**
     * Returns all applications submitted to a specific drive.
     */
    public List<Application> getApplicationsForDrive(String driveId) {
        return applicationManager.getByDrive(driveId);
    }

    /**
     * Updates the status and remarks of an existing application.
     */
    public void updateApplicationStatus(String appId, ApplicationStatus status, String remarks) {
        Application existing = applicationManager.getById(appId);
        if (existing == null) {
            throw new InvalidInputException("Application not found: " + appId);
        }
        applicationManager.updateStatus(appId, status, remarks);
    }

    /**
     * Withdraws an application on behalf of the student.
     */
    public void withdrawApplication(String appId) {
        Application existing = applicationManager.getById(appId);
        if (existing == null) {
            throw new InvalidInputException("Application not found: " + appId);
        }
        applicationManager.withdraw(appId);
    }

    public ApplicationManager getManager() {
        return applicationManager;
    }

    public void setNextAppId(int id) {
        this.nextAppId = Math.max(1, id);
    }
}
