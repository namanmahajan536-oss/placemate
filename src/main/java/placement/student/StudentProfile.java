package placement.student;

/**
 * Wrapper class for displaying student profile information.
 * Demonstrates composition pattern.
 */
public class StudentProfile {
    private final Student student;

    public StudentProfile(Student student) {
        this.student = student;
    }

    public String getFormattedProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║         STUDENT PROFILE              ║\n");
        sb.append("╠══════════════════════════════════════╣\n");
        sb.append(String.format("  Name           : %s\n", student.getName()));
        sb.append(String.format("  Roll Number    : %s\n", student.getRollNumber()));
        sb.append(String.format("  Branch         : %s\n", student.getBranch()));
        sb.append(String.format("  CGPA           : %.2f\n", student.getCgpa()));
        sb.append(String.format("  Graduation Year: %d\n", student.getGraduationYear()));
        sb.append(String.format("  Email          : %s\n", student.getEmail()));
        sb.append(String.format("  Active Backlogs: %d\n", student.getActiveBacklogs()));
        sb.append(String.format("  Placed         : %s\n", student.isPlaced() ? "Yes - " + student.getPlacedCompany() : "No"));
        if (student.isPlaced()) {
            sb.append(String.format("  Package        : %.2f LPA\n", student.getPackageOffered()));
        }
        sb.append("╚══════════════════════════════════════╝");
        return sb.toString();
    }

    public String getSummary() {
        return student.getName() + " | " + student.getRollNumber() + " | " +
               student.getBranch() + " | CGPA: " + student.getCgpa() +
               (student.isPlaced() ? " | Placed at " + student.getPlacedCompany() : " | Not Placed");
    }

    public String getSkillsSummary() {
        if (student.getSkills().isEmpty()) return "No skills added.";
        StringBuilder sb = new StringBuilder("Skills:\n");
        for (Skill s : student.getSkills()) {
            sb.append("  - ").append(s.toString()).append("\n");
        }
        return sb.toString();
    }

    public String getAcademicInfo() {
        return String.format("Branch: %s | CGPA: %.2f | Year: %d | Backlogs: %d active, %d historical",
                student.getBranch(), student.getCgpa(), student.getGraduationYear(),
                student.getActiveBacklogs(), student.getHistoricalBacklogs());
    }
}
