package placement.eligibility;

import java.util.List;

/**
 * Validates student branch against allowed branches.
 */
public class BranchValidator {
    public boolean validate(String studentBranch, List<String> allowedBranches) {
        if (allowedBranches == null || allowedBranches.isEmpty()) return true;
        for (String branch : allowedBranches) {
            if (branch.equalsIgnoreCase(studentBranch)) return true;
        }
        return false;
    }

    public String getReason(String studentBranch, List<String> allowedBranches) {
        return String.format("Branch '%s' is not in allowed branches: %s", studentBranch, allowedBranches);
    }
}
