package placement.ui;

/**
 * Helper class for printing formatted terminal menus.
 */
public class ConsoleMenu {

    public static void showMainMenu() {
        System.out.println("\n========================================");
        System.out.println("   PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("  1. Student Login");
        System.out.println("  2. Student Registration");
        System.out.println("  3. TPO / Admin Login");
        System.out.println("  4. Recruiter Login");
        System.out.println("  5. Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
    }

    public static void showStudentMenu(String name) {
        System.out.println("\n--- Student Dashboard [" + name + "] ---");
        System.out.println("  1.  View Profile");
        System.out.println("  2.  Update Profile");
        System.out.println("  3.  Add/Update Skills");
        System.out.println("  4.  View Placement Drives");
        System.out.println("  5.  Check Eligibility for Drive");
        System.out.println("  6.  Apply for Placement Drive");
        System.out.println("  7.  Track My Applications");
        System.out.println("  8.  View Interview Schedule");
        System.out.println("  9.  View My Results");
        System.out.println("  10. View Notifications");
        System.out.println("  11. Logout");
        System.out.print("Enter your choice: ");
    }

    public static void showTPOMenu() {
        System.out.println("\n--- TPO / Admin Dashboard ---");
        System.out.println("  1.  View All Students");
        System.out.println("  2.  Search Students");
        System.out.println("  3.  Manage Companies");
        System.out.println("  4.  Create Placement Drive");
        System.out.println("  5.  View All Drives");
        System.out.println("  6.  View Eligible Students for Drive");
        System.out.println("  7.  Manage Applications");
        System.out.println("  8.  Schedule Interview");
        System.out.println("  9.  Update Selection Results");
        System.out.println("  10. View Placement Statistics");
        System.out.println("  11. Manage Announcements");
        System.out.println("  12. Start Web Server");
        System.out.println("  13. Logout");
        System.out.print("Enter your choice: ");
    }

    public static void showRecruiterMenu(String name) {
        System.out.println("\n--- Recruiter Dashboard [" + name + "] ---");
        System.out.println("  1. View Company Profile");
        System.out.println("  2. Update Company Profile");
        System.out.println("  3. Create Job Posting");
        System.out.println("  4. View Applicants for Drives");
        System.out.println("  5. Filter Candidates");
        System.out.println("  6. Shortlist/Reject Candidates");
        System.out.println("  7. View Hiring Statistics");
        System.out.println("  8. Logout");
        System.out.print("Enter your choice: ");
    }

    public static void showSubMenu(String title, String[] options) {
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        System.out.print("Enter your choice: ");
    }
}
