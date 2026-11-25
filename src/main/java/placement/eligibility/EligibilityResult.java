package placement.eligibility;

import java.util.*;

/**
 * Result of an eligibility check with detailed reasons.
 */
public class EligibilityResult {
    private String studentId, driveId;
    private boolean isEligible;
    private List<String> reasons;
    private String checkedAt;

    public EligibilityResult(String studentId, String driveId) {
        this.studentId = studentId; this.driveId = driveId;
        this.isEligible = true; this.reasons = new ArrayList<>();
        this.checkedAt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    public void addReason(String reason) { reasons.add(reason); isEligible = false; }

    public String getFormattedResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(isEligible ? "✓ ELIGIBLE" : "✗ NOT ELIGIBLE").append("\n");
        if (!isEligible) {
            sb.append("Reasons:\n");
            for (String r : reasons) sb.append("  - ").append(r).append("\n");
        }
        return sb.toString();
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getDriveId() { return driveId; }
    public boolean isEligible() { return isEligible; }
    public List<String> getReasons() { return reasons; }
    public String getCheckedAt() { return checkedAt; }
    public void setEligible(boolean e) { this.isEligible = e; }
}
