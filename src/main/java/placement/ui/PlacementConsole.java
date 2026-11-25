package placement.ui;

import placement.authentication.*;
import placement.student.*;
import placement.company.*;
import placement.eligibility.*;
import placement.application.*;
import placement.interview.*;
import placement.results.*;
import placement.utilities.*;
import placement.server.HttpApiServer;

import java.util.*;

/**
 * Terminal Console Interface for PLACEMENT Management System.
 * Fully interactive and supports Student, TPO/Admin, and Recruiter roles.
 */
public class PlacementConsole {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleInput input = new ConsoleInput(scanner);

    private final DataManager dataManager = DataManager.getInstance();
    private final StudentManager studentManager = new StudentManager();
    private final StudentService studentService = new StudentService(studentManager);
    private final CompanyManager companyManager = new CompanyManager();
    private final CompanyService companyService = new CompanyService(companyManager);
    private final PlacementDriveManager driveManager = new PlacementDriveManager();
    private final RecruiterManager recruiterManager = new RecruiterManager();
    private final ApplicationManager applicationManager = new ApplicationManager();
    private final ApplicationService applicationService = new ApplicationService(applicationManager, studentManager, driveManager);
    private final ApplicationTracker applicationTracker = new ApplicationTracker(applicationManager);
    private final ShortlistingManager shortlistingManager = new ShortlistingManager(applicationManager);
    private final InterviewManager interviewManager = new InterviewManager();
    private final InterviewScheduler interviewScheduler = new InterviewScheduler();
    private final ResultManager resultManager = new ResultManager();
    private final PlacementStatistics placementStats = new PlacementStatistics();
    private final PackageAnalyzer packageAnalyzer = new PackageAnalyzer();
    private final NotificationManager notificationManager = new NotificationManager();
    private final EligibilityChecker eligibilityChecker = new EligibilityChecker();
    private final EligibilityService eligibilityService = new EligibilityService(eligibilityChecker, studentManager, driveManager);
    private final AuthenticationService authService = new AuthenticationService();

    private User currentUser = null;

    public PlacementConsole() {
        dataManager.setDataDir("data");
        loadData();
        seedIfEmpty();
    }

    private void loadData() {
        List<Student> sList = dataManager.loadStudents();
        if (!sList.isEmpty()) studentManager.setStudents(sList);

        List<Company> cList = dataManager.loadCompanies();
        if (!cList.isEmpty()) companyManager.setCompanies(cList);

        List<PlacementDrive> dList = dataManager.loadDrives();
        if (!dList.isEmpty()) driveManager.setDrives(dList);

        List<Recruiter> rList = dataManager.loadRecruiters();
        if (!rList.isEmpty()) recruiterManager.setRecruiters(rList);

        List<Application> aList = dataManager.loadApplications();
        if (!aList.isEmpty()) applicationManager.setApplications(aList);

        List<Interview> iList = dataManager.loadInterviews();
        if (!iList.isEmpty()) interviewManager.setInterviews(iList);

        List<PlacementResult> resList = dataManager.loadResults();
        if (!resList.isEmpty()) resultManager.setResults(resList);
    }

    private void saveData() {
        dataManager.saveStudents(studentManager.getAllStudents());
        dataManager.saveCompanies(companyManager.getAll());
        dataManager.saveDrives(driveManager.getAllDrives());
        dataManager.saveRecruiters(recruiterManager.getAllRecruiters());
        dataManager.saveApplications(applicationManager.getAllApplications());
        dataManager.saveInterviews(interviewManager.getAllInterviews());
        dataManager.saveResults(resultManager.getAllResults());
    }

    private void seedIfEmpty() {
        if (studentManager.getStudentCount() == 0) {
            Student s1 = new Student("STU001", "student1", PasswordManager.hashPassword("pass123"),
                    "rahul@campus.edu", "CS2024001", "Rahul Sharma", "CSE", 8.5, 2025);
            s1.addSkill(new Skill("Java", Skill.ProficiencyLevel.ADVANCED, 2.5));
            s1.addSkill(new Skill("DSA", Skill.ProficiencyLevel.ADVANCED, 2.0));
            s1.addSkill(new Skill("Spring Boot", Skill.ProficiencyLevel.INTERMEDIATE, 1.0));
            studentManager.addStudent(s1);

            Student s2 = new Student("STU002", "student2", PasswordManager.hashPassword("pass123"),
                    "priya@campus.edu", "EC2024001", "Priya Patel", "ECE", 7.8, 2025);
            s2.addSkill(new Skill("Embedded C", Skill.ProficiencyLevel.ADVANCED, 2.0));
            s2.addSkill(new Skill("Python", Skill.ProficiencyLevel.INTERMEDIATE, 1.5));
            studentManager.addStudent(s2);

            Student s3 = new Student("STU003", "student3", PasswordManager.hashPassword("pass123"),
                    "amit@campus.edu", "ME2024001", "Amit Kumar", "ME", 6.8, 2025);
            s3.addSkill(new Skill("AutoCAD", Skill.ProficiencyLevel.ADVANCED, 2.0));
            s3.addSkill(new Skill("SolidWorks", Skill.ProficiencyLevel.INTERMEDIATE, 1.0));
            studentManager.addStudent(s3);
        }

        if (companyManager.getCompanyCount() == 0) {
            Company c1 = new Company("COMP001", "TechCorp Solutions", "Information Technology", "Bangalore");
            c1.setMinCGPA(7.0);
            c1.setAllowedBranches(Arrays.asList("CSE", "ECE", "IT"));
            c1.setContactEmail("careers@techcorp.com");
            c1.setWebsite("https://techcorp.example.com");
            c1.setDescription("Leading global enterprise software and AI solutions provider.");
            companyManager.addCompany(c1);

            Company c2 = new Company("COMP002", "InnovateTech Labs", "Software & FinTech", "Hyderabad");
            c2.setMinCGPA(7.5);
            c2.setAllowedBranches(Arrays.asList("CSE", "IT"));
            c2.setContactEmail("hr@innovatetech.com");
            c2.setWebsite("https://innovatetech.example.com");
            c2.setDescription("Pioneering high-frequency fintech systems and cloud infrastructure.");
            companyManager.addCompany(c2);

            Company c3 = new Company("COMP003", "Apex Dynamics", "Core Engineering", "Pune");
            c3.setMinCGPA(6.5);
            c3.setAllowedBranches(Arrays.asList("ME", "ECE", "CE"));
            c3.setContactEmail("talent@apexdynamics.com");
            c3.setWebsite("https://apexdynamics.example.com");
            c3.setDescription("Robotics, automation, and advanced manufacturing core firm.");
            companyManager.addCompany(c3);
        }

        if (driveManager.getDriveCount() == 0) {
            PlacementDrive d1 = new PlacementDrive();
            d1.setId("DRV001");
            d1.setCompanyId("COMP001");
            d1.setCompanyName("TechCorp Solutions");
            d1.setTitle("Graduate Software Engineer Drive 2025");
            d1.setDescription("Recruitment for Associate Software Engineers in Java/FullStack.");
            d1.setDriveDate("2025-04-15");
            d1.setLastDateToApply("2025-04-10");
            d1.setMinCGPA(7.0);
            d1.setAllowedBranches(Arrays.asList("CSE", "ECE", "IT"));
            d1.setMaxActiveBacklogs(0);
            d1.setRequiredGraduationYear(2025);
            d1.setStatus(PlacementDrive.DriveStatus.UPCOMING);
            driveManager.createDrive(d1);

            PlacementDrive d2 = new PlacementDrive();
            d2.setId("DRV002");
            d2.setCompanyId("COMP002");
            d2.setCompanyName("InnovateTech Labs");
            d2.setTitle("FinTech Platform Engineer Drive");
            d2.setDescription("Hiring high-performance backend developers.");
            d2.setDriveDate("2025-05-01");
            d2.setLastDateToApply("2025-04-25");
            d2.setMinCGPA(7.5);
            d2.setAllowedBranches(Arrays.asList("CSE", "IT"));
            d2.setMaxActiveBacklogs(0);
            d2.setRequiredGraduationYear(2025);
            d2.setStatus(PlacementDrive.DriveStatus.UPCOMING);
            driveManager.createDrive(d2);
        }

        if (recruiterManager.getAllRecruiters().isEmpty()) {
            Recruiter r1 = new Recruiter("REC001", "recruiter1", PasswordManager.hashPassword("pass123"),
                    "recruiter@techcorp.com", "Vikram Malhotra", "COMP001", "Lead Technical Recruiter");
            recruiterManager.addRecruiter(r1);
        }

        notificationManager.addNotification("ALL", "Welcome to PLACEMENT Campus Management System!");
        notificationManager.addNotification("STU001", "New drive announced: TechCorp Graduate Engineer 2025");
        saveData();
    }

    public void run() {
        System.out.println("=======================================================");
        System.out.println(" Welcome to PLACEMENT - Smart Campus Management System ");
        System.out.println("=======================================================");

        boolean running = true;
        while (running) {
            ConsoleMenu.showMainMenu();
            int choice = input.readInt("", 1, 5);
            switch (choice) {
                case 1: handleStudentLogin(); break;
                case 2: handleStudentRegistration(); break;
                case 3: handleTPOLogin(); break;
                case 4: handleRecruiterLogin(); break;
                case 5:
                    saveData();
                    System.out.println("\nThank you for using PLACEMENT. Goodbye!");
                    running = false;
                    break;
                default: break;
            }
        }
    }

    private void handleStudentLogin() {
        System.out.println("\n--- Student Login ---");
        String username = input.readString("Username: ");
        String password = input.readString("Password: ");

        Student student = studentManager.getStudentByUsername(username);
        if (student != null && PasswordManager.verifyPassword(password, student.getPasswordHash())) {
            currentUser = student;
            System.out.println("Login successful! Welcome, " + student.getName());
            studentDashboard((Student) currentUser);
        } else {
            System.out.println("Invalid username or password. (Hint: demo user 'student1', pass 'pass123')");
        }
    }

    private void handleStudentRegistration() {
        System.out.println("\n--- Student Registration ---");
        String username = input.readString("Desired Username: ");
        if (studentManager.getStudentByUsername(username) != null) {
            System.out.println("Error: Username already taken.");
            return;
        }
        String password = input.readString("Password: ");
        String name = input.readString("Full Name: ");
        String email = input.readString("Email: ");
        String roll = input.readString("Roll Number (e.g. CS2025001): ");
        System.out.println("Branches: CSE, ECE, ME, CE, EE, IT");
        String branch = input.readString("Branch: ").toUpperCase();
        double cgpa = input.readDouble("Current CGPA (0.0 - 10.0): ", 0.0, 10.0);
        int gradYear = input.readInt("Graduation Year (e.g. 2025): ", 2020, 2030);

        String id = "STU" + String.format("%03d", studentManager.getStudentCount() + 1);
        Student s = new Student(id, username, PasswordManager.hashPassword(password), email, roll, name, branch, cgpa, gradYear);
        studentManager.addStudent(s);
        saveData();
        System.out.println("Registration successful! You can now login with your credentials.");
    }

    private void handleTPOLogin() {
        System.out.println("\n--- TPO / Admin Login ---");
        String username = input.readString("Username: ");
        String password = input.readString("Password: ");

        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            System.out.println("Login successful! Welcome, Placement Officer / Admin.");
            tpoDashboard();
        } else {
            System.out.println("Invalid Admin credentials! (Default: admin / admin123)");
        }
    }

    private void handleRecruiterLogin() {
        System.out.println("\n--- Recruiter Login ---");
        String username = input.readString("Username: ");
        String password = input.readString("Password: ");

        Recruiter recruiter = recruiterManager.getByUsername(username);
        if (recruiter != null && PasswordManager.verifyPassword(password, recruiter.getPasswordHash())) {
            currentUser = recruiter;
            System.out.println("Login successful! Welcome, " + recruiter.getName());
            recruiterDashboard(recruiter);
        } else {
            System.out.println("Invalid recruiter credentials! (Hint: recruiter1 / pass123)");
        }
    }

    // ----------------------------------------------------
    // STUDENT DASHBOARD
    // ----------------------------------------------------
    private void studentDashboard(Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleMenu.showStudentMenu(student.getName());
            int choice = input.readInt("", 1, 11);
            switch (choice) {
                case 1:
                    System.out.println(new StudentProfile(student).getFormattedProfile());
                    break;
                case 2:
                    updateStudentProfile(student);
                    break;
                case 3:
                    manageStudentSkills(student);
                    break;
                case 4:
                    viewPlacementDrives();
                    break;
                case 5:
                    checkStudentEligibility(student);
                    break;
                case 6:
                    applyForDrive(student);
                    break;
                case 7:
                    trackStudentApplications(student);
                    break;
                case 8:
                    viewStudentInterviews(student);
                    break;
                case 9:
                    viewStudentResults(student);
                    break;
                case 10:
                    viewNotifications(student.getId());
                    break;
                case 11:
                    saveData();
                    loggedIn = false;
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
            }
        }
    }

    private void updateStudentProfile(Student student) {
        System.out.println("\n--- Update Profile ---");
        System.out.println("Leave blank to keep existing value.");
        String newName = input.readString("Name [" + student.getName() + "]: ");
        if (!newName.isEmpty()) student.setName(newName);

        String newCgpaStr = input.readString("CGPA [" + student.getCgpa() + "]: ");
        if (!newCgpaStr.isEmpty()) {
            try { student.setCgpa(Double.parseDouble(newCgpaStr)); } catch (Exception ignored) {}
        }

        String newBacklogsStr = input.readString("Active Backlogs [" + student.getActiveBacklogs() + "]: ");
        if (!newBacklogsStr.isEmpty()) {
            try { student.setActiveBacklogs(Integer.parseInt(newBacklogsStr)); } catch (Exception ignored) {}
        }
        studentManager.updateStudent(student);
        saveData();
        System.out.println("Profile updated successfully!");
    }

    private void manageStudentSkills(Student student) {
        System.out.println("\n--- Manage Skills ---");
        System.out.println(new StudentProfile(student).getSkillsSummary());
        System.out.println("1. Add Skill  |  2. Back");
        int c = input.readInt("Choice: ", 1, 2);
        if (c == 1) {
            String name = input.readString("Skill Name (e.g. Java, Python, React): ");
            System.out.println("1. BEGINNER  2. INTERMEDIATE  3. ADVANCED");
            int lvl = input.readInt("Level: ", 1, 3);
            Skill.ProficiencyLevel pLevel = lvl == 1 ? Skill.ProficiencyLevel.BEGINNER :
                    lvl == 2 ? Skill.ProficiencyLevel.INTERMEDIATE : Skill.ProficiencyLevel.ADVANCED;
            double yrs = input.readDouble("Years of Experience: ", 0.0, 20.0);
            student.addSkill(new Skill(name, pLevel, yrs));
            studentManager.updateStudent(student);
            saveData();
            System.out.println("Skill added!");
        }
    }

    private void viewPlacementDrives() {
        System.out.println("\n--- Active & Upcoming Placement Drives ---");
        List<PlacementDrive> drives = driveManager.getAllDrives();
        if (drives.isEmpty()) {
            System.out.println("No placement drives available currently.");
            return;
        }
        System.out.printf("%-10s | %-24s | %-12s | %-10s | %-8s\n", "Drive ID", "Title", "Company", "Date", "Min CGPA");
        System.out.println("-------------------------------------------------------------------------");
        for (PlacementDrive d : drives) {
            System.out.printf("%-10s | %-24s | %-12s | %-10s | %-8.2f\n",
                    d.getId(), d.getTitle(), d.getCompanyName(), d.getDriveDate(), d.getMinCGPA());
        }
    }

    private void checkStudentEligibility(Student student) {
        viewPlacementDrives();
        String driveId = input.readString("\nEnter Drive ID to check eligibility: ");
        PlacementDrive drive = driveManager.getDriveById(driveId);
        if (drive == null) {
            System.out.println("Drive not found.");
            return;
        }
        EligibilityResult res = eligibilityChecker.checkEligibility(student, drive);
        System.out.println("\n" + res.getFormattedResult());
    }

    private void applyForDrive(Student student) {
        viewPlacementDrives();
        String driveId = input.readString("\nEnter Drive ID to apply: ");
        try {
            Application app = applicationService.submitApplication(student.getId(), driveId);
            notificationManager.addNotification(student.getId(), "Application submitted for " + app.getCompanyName());
            saveData();
            System.out.println("SUCCESS! Application submitted. Application ID: " + app.getId());
        } catch (Exception e) {
            System.out.println("Application failed: " + e.getMessage());
        }
    }

    private void trackStudentApplications(Student student) {
        System.out.println("\n--- My Applications ---");
        List<Application> apps = applicationManager.getByStudent(student.getId());
        if (apps.isEmpty()) {
            System.out.println("You have not applied to any drives yet.");
            return;
        }
        System.out.printf("%-10s | %-20s | %-18s | %-12s\n", "App ID", "Company", "Status", "Applied Date");
        System.out.println("-------------------------------------------------------------------");
        for (Application a : apps) {
            System.out.printf("%-10s | %-20s | %-18s | %-12s\n",
                    a.getId(), a.getCompanyName(), a.getStatus().getDisplayName(), a.getAppliedAt());
        }
    }

    private void viewStudentInterviews(Student student) {
        System.out.println("\n--- My Scheduled Interviews ---");
        List<Interview> list = interviewManager.getByStudent(student.getId());
        if (list.isEmpty()) {
            System.out.println("No upcoming interviews scheduled.");
            return;
        }
        for (Interview i : list) {
            System.out.println(" - " + i.toString() + " @ " + i.getVenue());
        }
    }

    private void viewStudentResults(Student student) {
        System.out.println("\n--- My Placement Results ---");
        List<PlacementResult> list = resultManager.getByStudent(student.getId());
        if (list.isEmpty()) {
            System.out.println("No placement offers/results recorded yet.");
            return;
        }
        for (PlacementResult r : list) {
            System.out.println(" ★ " + r.toString());
        }
    }

    private void viewNotifications(String userId) {
        System.out.println("\n--- Notifications ---");
        List<NotificationManager.Notification> list = notificationManager.getForUser(userId);
        if (list.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }
        for (NotificationManager.Notification n : list) {
            System.out.println("[" + n.getTimestamp() + "] " + n.getMessage());
            n.setRead(true);
        }
    }

    // ----------------------------------------------------
    // TPO / ADMIN DASHBOARD
    // ----------------------------------------------------
    private void tpoDashboard() {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleMenu.showTPOMenu();
            int choice = input.readInt("", 1, 13);
            switch (choice) {
                case 1:
                    listAllStudents();
                    break;
                case 2:
                    searchStudents();
                    break;
                case 3:
                    manageCompaniesMenu();
                    break;
                case 4:
                    createPlacementDrive();
                    break;
                case 5:
                    viewPlacementDrives();
                    break;
                case 6:
                    viewEligibleStudentsForDrive();
                    break;
                case 7:
                    manageApplicationsTPO();
                    break;
                case 8:
                    scheduleInterviewTPO();
                    break;
                case 9:
                    updateResultsTPO();
                    break;
                case 10:
                    showPlacementStats();
                    break;
                case 11:
                    broadcastAnnouncement();
                    break;
                case 12:
                    startWebServer();
                    break;
                case 13:
                    saveData();
                    loggedIn = false;
                    System.out.println("TPO logged out.");
                    break;
            }
        }
    }

    private void listAllStudents() {
        System.out.println("\n--- All Registered Students ---");
        List<Student> list = studentManager.getAllStudents();
        System.out.printf("%-10s | %-12s | %-18s | %-6s | %-6s | %-10s\n",
                "ID", "Roll No", "Name", "Branch", "CGPA", "Status");
        System.out.println("----------------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("%-10s | %-12s | %-18s | %-6s | %-6.2f | %-10s\n",
                    s.getId(), s.getRollNumber(), s.getName(), s.getBranch(), s.getCgpa(),
                    s.isPlaced() ? "PLACED" : "UNPLACED");
        }
    }

    private void searchStudents() {
        String q = input.readString("\nEnter student name, roll number, or branch: ");
        List<Student> results = studentManager.searchStudents(q);
        if (results.isEmpty()) {
            System.out.println("No matching students found.");
            return;
        }
        for (Student s : results) {
            System.out.println(new StudentProfile(s).getSummary());
        }
    }

    private void manageCompaniesMenu() {
        System.out.println("\n--- Manage Companies ---");
        System.out.println("1. View All Companies  2. Add New Company  3. Back");
        int c = input.readInt("Choice: ", 1, 3);
        if (c == 1) {
            for (Company comp : companyManager.getAll()) {
                System.out.println(" - [" + comp.getId() + "] " + comp.getName() + " (" + comp.getIndustry() + ") | Min CGPA: " + comp.getMinCGPA());
            }
        } else if (c == 2) {
            String id = "COMP" + String.format("%03d", companyManager.getCompanyCount() + 1);
            String name = input.readString("Company Name: ");
            String industry = input.readString("Industry: ");
            String location = input.readString("Location: ");
            double minCgpa = input.readDouble("Min CGPA: ", 0.0, 10.0);
            String branches = input.readString("Allowed Branches (comma separated, e.g. CSE,ECE,IT): ");
            Company comp = new Company(id, name, industry, location);
            comp.setMinCGPA(minCgpa);
            comp.setAllowedBranches(Arrays.asList(branches.split(",")));
            companyManager.addCompany(comp);
            saveData();
            System.out.println("Company added successfully! ID: " + id);
        }
    }

    private void createPlacementDrive() {
        System.out.println("\n--- Create Placement Drive ---");
        List<Company> comps = companyManager.getAll();
        for (int i = 0; i < comps.size(); i++) {
            System.out.println((i + 1) + ". " + comps.get(i).getName() + " (" + comps.get(i).getId() + ")");
        }
        int compIdx = input.readInt("Select Company: ", 1, comps.size()) - 1;
        Company comp = comps.get(compIdx);

        String id = "DRV" + String.format("%03d", driveManager.getDriveCount() + 1);
        String title = input.readString("Drive Title: ");
        String desc = input.readString("Description: ");
        String date = input.readString("Drive Date (YYYY-MM-DD): ");
        String lastDate = input.readString("Last Date to Apply (YYYY-MM-DD): ");
        double minCgpa = input.readDouble("Minimum CGPA: ", 0.0, 10.0);
        int maxBacklogs = input.readInt("Max Active Backlogs: ", 0, 10);
        int gradYear = input.readInt("Target Graduation Year: ", 2020, 2030);

        PlacementDrive d = new PlacementDrive();
        d.setId(id);
        d.setCompanyId(comp.getId());
        d.setCompanyName(comp.getName());
        d.setTitle(title);
        d.setDescription(desc);
        d.setDriveDate(date);
        d.setLastDateToApply(lastDate);
        d.setMinCGPA(minCgpa);
        d.setAllowedBranches(comp.getAllowedBranches());
        d.setMaxActiveBacklogs(maxBacklogs);
        d.setRequiredGraduationYear(gradYear);
        d.setStatus(PlacementDrive.DriveStatus.UPCOMING);

        driveManager.createDrive(d);
        notificationManager.addNotification("ALL", "New Drive Announced: " + title + " by " + comp.getName());
        saveData();
        System.out.println("Drive created successfully! ID: " + id);
    }

    private void viewEligibleStudentsForDrive() {
        viewPlacementDrives();
        String driveId = input.readString("\nEnter Drive ID: ");
        List<Student> eligible = eligibilityService.getEligibleStudents(driveId);
        System.out.println("\nFound " + eligible.size() + " eligible student(s):");
        for (Student s : eligible) {
            System.out.println(" - " + s.getRollNumber() + " | " + s.getName() + " | " + s.getBranch() + " | CGPA: " + s.getCgpa());
        }
    }

    private void manageApplicationsTPO() {
        System.out.println("\n--- Manage Applications ---");
        List<Application> all = applicationManager.getAllApplications();
        if (all.isEmpty()) {
            System.out.println("No applications submitted yet.");
            return;
        }
        for (Application a : all) {
            System.out.printf("[%s] %s | Drive: %s | Student: %s (%s) | Status: %s\n",
                    a.getId(), a.getCompanyName(), a.getDriveId(), a.getStudentName(), a.getRollNumber(), a.getStatus());
        }
        String appId = input.readString("\nEnter App ID to update (or blank to cancel): ");
        if (appId.isEmpty()) return;
        System.out.println("1. SHORTLIST  2. REJECT  3. SCHEDULE ASSESSMENT  4. SELECT");
        int st = input.readInt("New Status: ", 1, 4);
        ApplicationStatus newSt = st == 1 ? ApplicationStatus.SHORTLISTED :
                st == 2 ? ApplicationStatus.REJECTED :
                st == 3 ? ApplicationStatus.ASSESSMENT_SCHEDULED : ApplicationStatus.SELECTED;
        String remarks = input.readString("Remarks: ");
        applicationManager.updateStatus(appId, newSt, remarks);
        saveData();
        System.out.println("Application updated!");
    }

    private void scheduleInterviewTPO() {
        System.out.println("\n--- Schedule Interview ---");
        String appId = input.readString("Application ID: ");
        Application app = applicationManager.getById(appId);
        if (app == null) {
            System.out.println("Application not found.");
            return;
        }
        String date = input.readString("Date (YYYY-MM-DD): ");
        String time = input.readString("Time (HH:MM e.g. 10:00 AM): ");

        if (interviewScheduler.hasConflict(app.getStudentId(), date, time, interviewManager.getAllInterviews())) {
            System.out.println("WARNING: Student already has a scheduled interview at that exact date and time!");
            boolean proceed = input.confirm("Proceed anyway?");
            if (!proceed) return;
        }

        String venue = input.readString("Venue / Online Link: ");
        System.out.println("1. TECHNICAL_ROUND_1  2. CODING_TEST  3. HR_ROUND  4. APTITUDE_TEST");
        int r = input.readInt("Round Type: ", 1, 4);
        InterviewRound round = r == 1 ? InterviewRound.TECHNICAL_ROUND_1 :
                r == 2 ? InterviewRound.CODING_TEST :
                r == 3 ? InterviewRound.HR_ROUND : InterviewRound.APTITUDE_TEST;

        String id = "INT" + String.format("%03d", interviewManager.getAllInterviews().size() + 1);
        Interview interview = new Interview(id, app.getId(), app.getStudentId(), app.getDriveId(),
                app.getCompanyName(), round, date, time, venue);
        interviewManager.addInterview(interview);
        interviewScheduler.scheduleInterview(interview);
        notificationManager.addNotification(app.getStudentId(), "Interview scheduled with " + app.getCompanyName() + " on " + date + " " + time);
        saveData();
        System.out.println("Interview scheduled successfully! ID: " + id);
    }

    private void updateResultsTPO() {
        System.out.println("\n--- Update Placement Selection Results ---");
        String studentId = input.readString("Student ID (e.g. STU001): ");
        Student s = studentManager.getStudentById(studentId);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        String company = input.readString("Company Name: ");
        String role = input.readString("Job Role: ");
        double pkg = input.readDouble("Package (LPA): ", 1.0, 150.0);

        String id = "RES" + String.format("%03d", resultManager.getAllResults().size() + 1);
        PlacementResult res = new PlacementResult(id, s.getId(), "DRV-MANUAL", company, pkg, role);
        res.setStudentName(s.getName());
        res.setRollNumber(s.getRollNumber());
        resultManager.addResult(res);

        s.setPlaced(true);
        s.setPlacedCompany(company);
        s.setPackageOffered(pkg);
        studentManager.updateStudent(s);

        notificationManager.addNotification(s.getId(), "CONGRATULATIONS! You have been selected by " + company + " (" + pkg + " LPA)!");
        saveData();
        System.out.println("Result recorded and student marked as PLACED!");
    }

    private void showPlacementStats() {
        placementStats.calculate(studentManager.getAllStudents(), resultManager.getAllResults());
        System.out.println("\n" + ReportManager.generatePlacementReport(placementStats));
    }

    private void broadcastAnnouncement() {
        String msg = input.readString("Enter announcement message: ");
        notificationManager.addNotification("ALL", msg);
        System.out.println("Announcement broadcasted to all students!");
    }

    private void startWebServer() {
        try {
            HttpApiServer server = new HttpApiServer(8080);
            server.start();
            System.out.println("Open http://localhost:8080/ in your web browser.");
        } catch (Exception e) {
            System.out.println("Server notice: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // RECRUITER DASHBOARD
    // ----------------------------------------------------
    private void recruiterDashboard(Recruiter recruiter) {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleMenu.showRecruiterMenu(recruiter.getName());
            int choice = input.readInt("", 1, 8);
            switch (choice) {
                case 1:
                    Company c = companyManager.getById(recruiter.getCompanyId());
                    if (c != null) {
                        System.out.println("\nCompany: " + c.getName());
                        System.out.println("Industry: " + c.getIndustry());
                        System.out.println("Location: " + c.getLocation());
                        System.out.println("Min CGPA: " + c.getMinCGPA());
                    } else {
                        System.out.println("Company profile not found.");
                    }
                    break;
                case 2:
                    System.out.println("Company update available via TPO interface.");
                    break;
                case 3:
                    System.out.println("\n--- Post Job Description ---");
                    String title = input.readString("Job Title: ");
                    double lpa = input.readDouble("Package (LPA): ", 1.0, 100.0);
                    System.out.println("Job posted for review.");
                    break;
                case 4:
                    System.out.println("\n--- Applicants for My Company ---");
                    for (Application a : applicationManager.getAllApplications()) {
                        if (recruiter.getCompanyId().equals(a.getCompanyId()) ||
                            (a.getCompanyName() != null && a.getCompanyName().contains("TechCorp"))) {
                            System.out.printf("[%s] %s (%s) | Status: %s\n",
                                    a.getId(), a.getStudentName(), a.getRollNumber(), a.getStatus());
                        }
                    }
                    break;
                case 5:
                    System.out.println("\n--- Filter Candidate Profiles ---");
                    double minCgpa = input.readDouble("Filter Minimum CGPA: ", 0.0, 10.0);
                    for (Student s : studentService.searchByCGPA(minCgpa)) {
                        System.out.println(" - " + s.getName() + " | " + s.getBranch() + " | CGPA: " + s.getCgpa());
                    }
                    break;
                case 6:
                    String appId = input.readString("Application ID to shortlist/reject: ");
                    System.out.println("1. Shortlist  2. Reject");
                    int act = input.readInt("Action: ", 1, 2);
                    applicationManager.updateStatus(appId, act == 1 ? ApplicationStatus.SHORTLISTED : ApplicationStatus.REJECTED, "Recruiter decision");
                    saveData();
                    System.out.println("Candidate status updated!");
                    break;
                case 7:
                    System.out.println("\n--- Hiring Summary ---");
                    System.out.println("Active Applicants: " + applicationManager.getAllApplications().size());
                    System.out.println("Selected Offers: " + resultManager.getAllResults().size());
                    break;
                case 8:
                    saveData();
                    loggedIn = false;
                    System.out.println("Recruiter logged out.");
                    break;
            }
        }
    }
}
