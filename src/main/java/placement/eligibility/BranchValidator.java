package placement.eligibility;

import java.util.List;

/**
 * Validates whether a student's academic branch is in a drive's allowed branches.
 * Performs case-insensitive matching.
 */
public class BranchValidator {
    private String lastReason;

    /**
     * Validates the student's branch against allowedBranches using case-insensitive comparison.
     * If allowedBranches is null or empty, all branches are treated as eligible.
     *
     * @param studentBranch   the student's branch (e.g., "CSE", "cse")
     * @param allowedBranches list of permitted branches
     * @return true if the student's branch is allowed, false otherwise
     */
    public boolean validate(String studentBranch, List<String> allowedBranches) {
        if (allowedBranches == null || allowedBranches.isEmpty()) {
            this.lastReason = "";
            return true;
        }
        if (studentBranch == null || studentBranch.trim().isEmpty()) {
            this.lastReason = getReason(studentBranch, allowedBranches);
            return false;
        }
        String normalizedStudentBranch = studentBranch.trim();
        for (String allowed : allowedBranches) {
            if (allowed != null && allowed.trim().equalsIgnoreCase(normalizedStudentBranch)) {
                this.lastReason = "";
                return true;
            }
        }
        this.lastReason = getReason(studentBranch, allowedBranches);
        return false;
    }

    /**
     * Generates a human-readable reason for a branch mismatch.
     */
    public String getReason(String studentBranch, List<String> allowedBranches) {
        String branchLabel = (studentBranch == null || studentBranch.trim().isEmpty()) ? "N/A" : studentBranch.trim();
        return String.format("Branch '%s' is not in allowed branches: %s", branchLabel, allowedBranches);
    }

    /**
     * Returns the reason from the most recent failed validation, or empty string if valid.
     */
    public String getLastReason() {
        return lastReason != null ? lastReason : "";
    }
}
