package placement.application;

/**
 * Enum representing all possible application stages.
 */
public enum ApplicationStatus {
    APPLIED("Applied"),
    UNDER_REVIEW("Under Review"),
    SHORTLISTED("Shortlisted"),
    ASSESSMENT_SCHEDULED("Assessment Scheduled"),
    TECHNICAL_INTERVIEW("Technical Interview"),
    HR_INTERVIEW("HR Interview"),
    SELECTED("Selected"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ApplicationStatus(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}
