package placement.interview;

/**
 * Enum for interview round types with display names and order.
 */
public enum InterviewRound {
    APTITUDE_TEST("Aptitude Test", 1),
    CODING_TEST("Coding Test", 2),
    TECHNICAL_ROUND_1("Technical Round 1", 3),
    TECHNICAL_ROUND_2("Technical Round 2", 4),
    GROUP_DISCUSSION("Group Discussion", 5),
    HR_ROUND("HR Round", 6);

    private final String displayName;
    private final int order;

    InterviewRound(String displayName, int order) {
        this.displayName = displayName; this.order = order;
    }

    public String getDisplayName() { return displayName; }
    public int getOrder() { return order; }

    @Override
    public String toString() { return displayName; }
}
