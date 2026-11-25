package placement.eligibility;

import placement.student.Student;
import placement.student.Skill;
import placement.company.PlacementDrive;
import java.util.*;

/**
 * Main eligibility checker using composition pattern.
 * Aggregates CGPAChecker, BacklogChecker, and BranchValidator.
 */
public class EligibilityChecker {
    private final CGPAChecker cgpaChecker = new CGPAChecker();
    private final BacklogChecker backlogChecker = new BacklogChecker();
    private final BranchValidator branchValidator = new BranchValidator();

    /**
     * Check if a student is eligible for a placement drive.
     * Returns detailed EligibilityResult with reasons for ineligibility.
     */
    public EligibilityResult checkEligibility(Student student, PlacementDrive drive) {
        EligibilityResult result = new EligibilityResult(student.getId(), drive.getId());

        // Check CGPA
        if (!cgpaChecker.check(student.getCgpa(), drive.getMinCGPA())) {
            result.addReason(cgpaChecker.getReason(student.getCgpa(), drive.getMinCGPA()));
        }

        // Check Branch
        if (!branchValidator.validate(student.getBranch(), drive.getAllowedBranches())) {
            result.addReason(branchValidator.getReason(student.getBranch(), drive.getAllowedBranches()));
        }

        // Check Active Backlogs
        if (!backlogChecker.checkActive(student.getActiveBacklogs(), drive.getMaxActiveBacklogs())) {
            result.addReason(backlogChecker.getActiveReason(student.getActiveBacklogs(), drive.getMaxActiveBacklogs()));
        }

        // Check Graduation Year
        if (drive.getRequiredGraduationYear() > 0 && student.getGraduationYear() != drive.getRequiredGraduationYear()) {
            result.addReason(String.format("Graduation year %d does not match required year %d",
                    student.getGraduationYear(), drive.getRequiredGraduationYear()));
        }

        // Check if already placed
        if (student.isPlaced()) {
            result.addReason("Student is already placed at " + student.getPlacedCompany());
        }

        return result;
    }
}
