package placement.eligibility;

import placement.company.PlacementDrive;
import placement.student.Skill;
import placement.student.Student;
import java.util.*;

/**
 * Evaluates full student eligibility for a placement drive using the Composition pattern.
 * Aggregates CGPAChecker, BacklogChecker, and BranchValidator, and evaluates rules
 * polymorphically via the internal EligibilityRule abstraction.
 */
public class EligibilityChecker {
    private final CGPAChecker cgpaChecker;
    private final BacklogChecker backlogChecker;
    private final BranchValidator branchValidator;

    /**
     * Abstraction representing a single eligibility rule evaluated polymorphically.
     */
    private interface EligibilityRule {
        void evaluate(Student student, EligibilityResult result);
    }

    public EligibilityChecker() {
        this.cgpaChecker = new CGPAChecker();
        this.backlogChecker = new BacklogChecker();
        this.branchValidator = new BranchValidator();
    }

    public EligibilityChecker(CGPAChecker cgpaChecker, BacklogChecker backlogChecker, BranchValidator branchValidator) {
        this.cgpaChecker = cgpaChecker != null ? cgpaChecker : new CGPAChecker();
        this.backlogChecker = backlogChecker != null ? backlogChecker : new BacklogChecker();
        this.branchValidator = branchValidator != null ? branchValidator : new BranchValidator();
    }

    /**
     * Checks whether a student satisfies all eligibility requirements of a PlacementDrive.
     * Runs every check (CGPA, branch, backlogs, graduation year, placement status) and
     * aggregates all failure reasons into the returned EligibilityResult.
     */
    public EligibilityResult checkEligibility(Student student, PlacementDrive drive) {
        String studentId = student != null ? student.getId() : "";
        String driveId = drive != null ? drive.getId() : "";
        EligibilityResult result = new EligibilityResult(studentId, driveId);

        if (student == null || drive == null) {
            result.addReason("Student or Placement Drive details are missing.");
            return result;
        }

        List<EligibilityRule> rules = Arrays.asList(
            (s, res) -> {
                if (!cgpaChecker.check(s.getCgpa(), drive.getMinCGPA())) {
                    res.addReason(cgpaChecker.getLastReason());
                }
            },
            (s, res) -> {
                if (!branchValidator.validate(s.getBranch(), drive.getAllowedBranches())) {
                    res.addReason(branchValidator.getLastReason());
                }
            },
            (s, res) -> {
                // PlacementDrive specifies maxActiveBacklogs; historical limit is unrestricted (-1) unless criteria sets it.
                if (!backlogChecker.check(s.getActiveBacklogs(), drive.getMaxActiveBacklogs(),
                        s.getHistoricalBacklogs(), -1)) {
                    res.addReason(backlogChecker.getLastReason());
                }
            },
            (s, res) -> {
                if (drive.getRequiredGraduationYear() > 0 && s.getGraduationYear() != drive.getRequiredGraduationYear()) {
                    res.addReason(String.format("Graduation year %d does not match required year %d",
                            s.getGraduationYear(), drive.getRequiredGraduationYear()));
                }
            },
            (s, res) -> {
                if (s.isPlaced()) {
                    res.addReason("Student is already placed at " + s.getPlacedCompany());
                }
            }
        );

        for (EligibilityRule rule : rules) {
            rule.evaluate(student, result);
        }

        return result;
    }

    /**
     * Overloaded check method evaluating a Student against a standalone EligibilityCriteria object.
     */
    public EligibilityResult checkEligibility(Student student, EligibilityCriteria criteria) {
        String studentId = student != null ? student.getId() : "";
        String driveId = criteria != null ? criteria.getDriveId() : "";
        EligibilityResult result = new EligibilityResult(studentId, driveId);

        if (student == null || criteria == null) {
            result.addReason("Student or Eligibility Criteria details are missing.");
            return result;
        }

        if (!cgpaChecker.check(student.getCgpa(), criteria.getMinCGPA())) {
            result.addReason(cgpaChecker.getLastReason());
        }
        if (!branchValidator.validate(student.getBranch(), criteria.getAllowedBranches())) {
            result.addReason(branchValidator.getLastReason());
        }
        if (!backlogChecker.check(student.getActiveBacklogs(), criteria.getMaxActiveBacklogs(),
                student.getHistoricalBacklogs(), criteria.getMaxHistoricalBacklogs())) {
            for (String reason : backlogChecker.getReasons(
                    student.getActiveBacklogs(), criteria.getMaxActiveBacklogs(),
                    student.getHistoricalBacklogs(), criteria.getMaxHistoricalBacklogs())) {
                result.addReason(reason);
            }
        }
        if (criteria.getRequiredGraduationYear() > 0
                && student.getGraduationYear() != criteria.getRequiredGraduationYear()) {
            result.addReason(String.format("Graduation year %d does not match required year %d",
                    student.getGraduationYear(), criteria.getRequiredGraduationYear()));
        }
        if (criteria.getRequiredSkills() != null && !criteria.getRequiredSkills().isEmpty()) {
            Set<String> studentSkills = new HashSet<>();
            if (student.getSkills() != null) {
                for (Skill sk : student.getSkills()) {
                    if (sk != null && sk.getName() != null) {
                        studentSkills.add(sk.getName().trim().toLowerCase());
                    }
                }
            }
            for (String required : criteria.getRequiredSkills()) {
                if (required != null && !required.trim().isEmpty()
                        && !studentSkills.contains(required.trim().toLowerCase())) {
                    result.addReason("Missing required skill: " + required.trim());
                }
            }
        }
        if (student.isPlaced()) {
            result.addReason("Student is already placed at " + student.getPlacedCompany());
        }

        return result;
    }

    public CGPAChecker getCgpaChecker() {
        return cgpaChecker;
    }

    public BacklogChecker getBacklogChecker() {
        return backlogChecker;
    }

    public BranchValidator getBranchValidator() {
        return branchValidator;
    }
}
