package placement.application;

/**
 * Enum representing all stages of a student's placement application lifecycle.
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

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this application stage.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns true if this status represents a final state in the recruitment pipeline.
     */
    public boolean isTerminal() {
        return this == SELECTED || this == REJECTED || this == WITHDRAWN;
    }

    /**
     * Safely parses a status string by enum name or display name, defaulting to APPLIED.
     */
    public static ApplicationStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return APPLIED;
        }
        String normalized = value.trim();
        for (ApplicationStatus status : values()) {
            if (status.name().equalsIgnoreCase(normalized)
                    || status.displayName.equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        return APPLIED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
