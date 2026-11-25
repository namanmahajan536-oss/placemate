package placement.eligibility;

import placement.company.PlacementDrive;
import placement.company.PlacementDriveManager;
import placement.student.Student;
import placement.student.StudentManager;
import java.util.*;

/**
 * Service layer coordinating eligibility checks across students and placement drives.
 * Uses EligibilityChecker internally and supports both manager-backed lookups and explicit student lists.
 */
public class EligibilityService {
    private final EligibilityChecker checker;
    private final StudentManager studentManager;
    private final PlacementDriveManager driveManager;
    private final Map<String, EligibilityCriteria> criteriaByDrive = new HashMap<>();

    public EligibilityService(EligibilityChecker checker, StudentManager studentManager, PlacementDriveManager driveManager) {
        this.checker = checker != null ? checker : new EligibilityChecker();
        this.studentManager = studentManager;
        this.driveManager = driveManager;
    }

    public EligibilityService(StudentManager studentManager, PlacementDriveManager driveManager) {
        this(new EligibilityChecker(), studentManager, driveManager);
    }

    /**
     * Registers optional extended EligibilityCriteria for a specific drive.
     */
    public void registerCriteria(EligibilityCriteria criteria) {
        if (criteria != null && criteria.getDriveId() != null && !criteria.getDriveId().isEmpty()) {
            criteriaByDrive.put(criteria.getDriveId(), criteria);
        }
    }

    /**
     * Checks eligibility of a single student for a single drive by looking up both IDs.
     */
    public EligibilityResult checkStudentEligibility(String studentId, String driveId) {
        Student student = studentManager != null ? studentManager.getStudentById(studentId) : null;
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;

        if (student == null || drive == null) {
            EligibilityResult result = new EligibilityResult(studentId, driveId);
            if (student == null) {
                result.addReason("Student not found: " + studentId);
            }
            if (drive == null) {
                result.addReason("Placement drive not found: " + driveId);
            }
            return result;
        }
        return evaluateStudentForDrive(student, drive);
    }

    /**
     * Evaluates a student against a drive (and any registered EligibilityCriteria).
     */
    private EligibilityResult evaluateStudentForDrive(Student student, PlacementDrive drive) {
        EligibilityCriteria extraCriteria = criteriaByDrive.get(drive.getId());
        if (extraCriteria != null) {
            return checker.checkEligibility(student, extraCriteria);
        }
        return checker.checkEligibility(student, drive);
    }

    /**
     * Returns all eligible students from StudentManager for the given drive, sorted by CGPA descending.
     */
    public List<Student> getEligibleStudents(String driveId) {
        List<Student> allStudents = studentManager != null ? studentManager.getAllStudents() : new ArrayList<>();
        return getEligibleStudents(driveId, allStudents);
    }

    /**
     * Filters the provided list of students to return those eligible for the drive, sorted by CGPA descending.
     */
    public List<Student> getEligibleStudents(String driveId, List<Student> students) {
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;
        if (drive == null || students == null) {
            return new ArrayList<>();
        }
        List<Student> eligible = new ArrayList<>();
        for (Student student : students) {
            if (student != null && evaluateStudentForDrive(student, drive).isEligible()) {
                eligible.add(student);
            }
        }
        // Sort eligible students by CGPA descending
        Collections.sort(eligible, (a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));
        return eligible;
    }

    /**
     * Returns all ineligible students from StudentManager for the given drive.
     */
    public List<Student> getIneligibleStudents(String driveId) {
        List<Student> allStudents = studentManager != null ? studentManager.getAllStudents() : new ArrayList<>();
        return getIneligibleStudents(driveId, allStudents);
    }

    /**
     * Filters the provided list of students to return those ineligible for the drive.
     */
    public List<Student> getIneligibleStudents(String driveId, List<Student> students) {
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;
        if (drive == null || students == null) {
            return new ArrayList<>();
        }
        List<Student> ineligible = new ArrayList<>();
        for (Student student : students) {
            if (student != null && !evaluateStudentForDrive(student, drive).isEligible()) {
                ineligible.add(student);
            }
        }
        return ineligible;
    }

    /**
     * Generates a map of studentId -> EligibilityResult for all students against a drive.
     */
    public Map<String, EligibilityResult> getEligibilityReport(String driveId) {
        List<Student> allStudents = studentManager != null ? studentManager.getAllStudents() : new ArrayList<>();
        return getEligibilityReport(driveId, allStudents);
    }

    /**
     * Generates a map of studentId -> EligibilityResult for the provided students against a drive.
     */
    public Map<String, EligibilityResult> getEligibilityReport(String driveId, List<Student> students) {
        PlacementDrive drive = driveManager != null ? driveManager.getDriveById(driveId) : null;
        if (drive == null || students == null) {
            return new LinkedHashMap<>();
        }
        Map<String, EligibilityResult> report = new LinkedHashMap<>();
        for (Student student : students) {
            if (student != null) {
                report.put(student.getId(), evaluateStudentForDrive(student, drive));
            }
        }
        return report;
    }

    public EligibilityChecker getChecker() {
        return checker;
    }
}
