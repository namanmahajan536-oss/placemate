package placement.eligibility;

import placement.student.Student;
import placement.student.StudentManager;
import placement.company.PlacementDrive;
import placement.company.PlacementDriveManager;
import java.util.*;

/**
 * Service layer for eligibility operations.
 */
public class EligibilityService {
    private final EligibilityChecker checker;
    private final StudentManager studentManager;
    private final PlacementDriveManager driveManager;

    public EligibilityService(EligibilityChecker checker, StudentManager studentManager, PlacementDriveManager driveManager) {
        this.checker = checker; this.studentManager = studentManager; this.driveManager = driveManager;
    }

    public EligibilityResult checkStudentEligibility(String studentId, String driveId) {
        Student student = studentManager.getStudentById(studentId);
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (student == null || drive == null) {
            EligibilityResult r = new EligibilityResult(studentId, driveId);
            r.addReason("Student or Drive not found.");
            return r;
        }
        return checker.checkEligibility(student, drive);
    }

    public List<Student> getEligibleStudents(String driveId) {
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (drive == null) return new ArrayList<>();
        List<Student> eligible = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (checker.checkEligibility(s, drive).isEligible()) eligible.add(s);
        }
        return eligible;
    }

    public List<Student> getIneligibleStudents(String driveId) {
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (drive == null) return new ArrayList<>();
        List<Student> ineligible = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (!checker.checkEligibility(s, drive).isEligible()) ineligible.add(s);
        }
        return ineligible;
    }

    public Map<String, EligibilityResult> getEligibilityReport(String driveId) {
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (drive == null) return new HashMap<>();
        Map<String, EligibilityResult> report = new LinkedHashMap<>();
        for (Student s : studentManager.getAllStudents()) {
            report.put(s.getId(), checker.checkEligibility(s, drive));
        }
        return report;
    }

    public EligibilityChecker getChecker() { return checker; }
}
