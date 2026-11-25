package placement.eligibility;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Holds the outcome of an eligibility evaluation for a student and a placement drive.
 * If the student is not eligible, the reasons list explains each failed condition.
 */
public class EligibilityResult {
    private String studentId;
    private String driveId;
    private boolean isEligible;
    private List<String> reasons;
    private String checkedAt;

    /**
     * Default constructor used during deserialization.
     */
    public EligibilityResult() {
        this.isEligible = true;
        this.reasons = new ArrayList<>();
        this.checkedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Creates a new EligibilityResult defaulting to eligible until a failure reason is added.
     */
    public EligibilityResult(String studentId, String driveId) {
        this();
        this.studentId = studentId;
        this.driveId = driveId;
    }

    /**
     * Records an ineligibility reason and marks the overall result as not eligible.
     */
    public void addReason(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            this.reasons.add(reason.trim());
            this.isEligible = false;
        }
    }

    /**
     * Returns a formatted, human-readable summary of the eligibility check.
     */
    public String getFormattedResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(isEligible ? "ELIGIBLE" : "NOT ELIGIBLE");
        sb.append(" (Checked at: ").append(checkedAt != null ? checkedAt : "").append(")\n");
        if (!isEligible && !reasons.isEmpty()) {
            sb.append("Reasons:\n");
            for (int i = 0; i < reasons.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(reasons.get(i)).append("\n");
            }
        }
        return sb.toString();
    }

    // ---- Getters and Setters ----

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        this.isEligible = eligible;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons != null ? new ArrayList<>(reasons) : new ArrayList<>();
        if (!this.reasons.isEmpty()) {
            this.isEligible = false;
        }
    }

    public String getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(String checkedAt) {
        this.checkedAt = checkedAt;
    }

    /**
     * Serializes this EligibilityResult to a Map for JSON/file storage.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("studentId", studentId != null ? studentId : "");
        map.put("driveId", driveId != null ? driveId : "");
        map.put("isEligible", String.valueOf(isEligible));
        map.put("reasons", reasons != null ? String.join(";", reasons) : "");
        map.put("checkedAt", checkedAt != null ? checkedAt : "");
        return map;
    }

    /**
     * Deserializes an EligibilityResult from a Map.
     */
    public static EligibilityResult fromMap(Map<String, String> map) {
        EligibilityResult result = new EligibilityResult();
        if (map == null) {
            return result;
        }
        result.studentId = map.getOrDefault("studentId", "");
        result.driveId = map.getOrDefault("driveId", "");
        result.isEligible = Boolean.parseBoolean(map.getOrDefault("isEligible", "true"));
        String reasonStr = map.getOrDefault("reasons", "");
        if (!reasonStr.trim().isEmpty()) {
            for (String r : reasonStr.split(";")) {
                if (!r.trim().isEmpty()) {
                    result.reasons.add(r.trim());
                }
            }
        }
        result.checkedAt = map.getOrDefault("checkedAt", result.checkedAt);
        return result;
    }

    @Override
    public String toString() {
        return getFormattedResult();
    }
}
