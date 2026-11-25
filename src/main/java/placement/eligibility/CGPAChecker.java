package placement.eligibility;

/**
 * Checks CGPA eligibility. Demonstrates Single Responsibility Principle.
 */
public class CGPAChecker {
    public boolean check(double studentCGPA, double minCGPA) {
        return studentCGPA >= minCGPA;
    }

    public String getReason(double studentCGPA, double minCGPA) {
        return String.format("CGPA %.2f is below minimum requirement of %.2f", studentCGPA, minCGPA);
    }
}
