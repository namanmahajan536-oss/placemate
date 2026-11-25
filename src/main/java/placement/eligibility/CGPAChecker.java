package placement.eligibility;

import java.util.Locale;

/**
 * Checks whether a student's CGPA meets the minimum required CGPA.
 * Demonstrates the Single Responsibility Principle (SRP): this class has one job only.
 */
public class CGPAChecker {
    private String lastReason;

    /**
     * Checks if studentCGPA is greater than or equal to minCGPA.
     * If the check fails, stores an explanatory reason string accessible via getLastReason().
     *
     * @param studentCGPA the student's current CGPA
     * @param minCGPA     the minimum CGPA required by the drive
     * @return true if eligible on CGPA, false otherwise
     */
    public boolean check(double studentCGPA, double minCGPA) {
        if (minCGPA <= 0.0) {
            this.lastReason = "";
            return true;
        }
        if (studentCGPA >= minCGPA) {
            this.lastReason = "";
            return true;
        }
        this.lastReason = getReason(studentCGPA, minCGPA);
        return false;
    }

    /**
     * Generates a descriptive failure reason when CGPA is below the minimum requirement.
     */
    public String getReason(double studentCGPA, double minCGPA) {
        return String.format(Locale.US, "CGPA %.2f is below minimum %.2f", studentCGPA, minCGPA);
    }

    /**
     * Returns the reason from the most recent failed check, or empty string if passed.
     */
    public String getLastReason() {
        return lastReason != null ? lastReason : "";
    }
}
