package placement.application;

import placement.student.Student;
import placement.student.StudentManager;
import placement.company.PlacementDrive;
import placement.company.PlacementDriveManager;
import placement.eligibility.EligibilityChecker;
import placement.eligibility.EligibilityResult;
import placement.exception.DuplicateRecordException;
import placement.exception.InvalidInputException;
import java.util.*;

/**
 * Business logic for application operations.
 */
public class ApplicationService {
    private final ApplicationManager applicationManager;
    private final StudentManager studentManager;
    private final PlacementDriveManager driveManager;
    private int nextAppId = 1;

    public ApplicationService(ApplicationManager applicationManager, StudentManager studentManager,
                              PlacementDriveManager driveManager) {
        this.applicationManager = applicationManager;
        this.studentManager = studentManager;
        this.driveManager = driveManager;
    }

    public Application submitApplication(String studentId, String driveId) {
        Student student = studentManager.getStudentById(studentId);
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (student == null) throw new InvalidInputException("Student not found.");
        if (drive == null) throw new InvalidInputException("Drive not found.");

        if (applicationManager.isDuplicate(studentId, driveId))
            throw new DuplicateRecordException("You have already applied to this drive.");

        // Check eligibility
        EligibilityChecker checker = new EligibilityChecker();
        EligibilityResult eligibility = checker.checkEligibility(student, drive);
        if (!eligibility.isEligible())
            throw new InvalidInputException("Not eligible: " + String.join(", ", eligibility.getReasons()));

        String appId = "APP" + String.format("%04d", nextAppId++);
        Application app = new Application(appId, studentId, driveId, drive.getCompanyId(), drive.getCompanyName());
        app.setStudentName(student.getName());
        app.setRollNumber(student.getRollNumber());
        applicationManager.submitApplication(app);
        return app;
    }

    public List<Application> trackApplications(String studentId) {
        return applicationManager.getByStudent(studentId);
    }

    public List<Application> getApplicationsForDrive(String driveId) {
        return applicationManager.getByDrive(driveId);
    }

    public void updateApplicationStatus(String appId, ApplicationStatus status, String remarks) {
        applicationManager.updateStatus(appId, status, remarks);
    }

    public ApplicationManager getManager() { return applicationManager; }

    public void setNextAppId(int id) { this.nextAppId = id; }
}
