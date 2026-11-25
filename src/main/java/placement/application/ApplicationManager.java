package placement.application;

import java.util.*;

/**
 * CRUD for applications with duplicate prevention using HashSet.
 * Demonstrates HashMap indexing and HashSet for uniqueness.
 */
public class ApplicationManager {
    private final List<Application> applications = new ArrayList<>();
    private final HashMap<String, List<Application>> studentIndex = new HashMap<>();
    private final HashSet<String> duplicateTracker = new HashSet<>(); // key = studentId_driveId

    public synchronized boolean submitApplication(Application app) {
        String key = app.getStudentId() + "_" + app.getDriveId();
        if (duplicateTracker.contains(key)) return false; // Duplicate prevention
        applications.add(app);
        duplicateTracker.add(key);
        studentIndex.computeIfAbsent(app.getStudentId(), k -> new ArrayList<>()).add(app);
        return true;
    }

    public List<Application> getByStudent(String studentId) {
        return studentIndex.getOrDefault(studentId, new ArrayList<>());
    }

    public List<Application> getByDrive(String driveId) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications) if (driveId.equals(a.getDriveId())) result.add(a);
        return result;
    }

    public List<Application> getByStatus(ApplicationStatus status) {
        List<Application> result = new ArrayList<>();
        for (Application a : applications) if (a.getStatus() == status) result.add(a);
        return result;
    }

    public synchronized void updateStatus(String appId, ApplicationStatus newStatus, String remarks) {
        for (Application a : applications) {
            if (a.getId().equals(appId)) {
                a.setStatus(newStatus);
                a.setRemarks(remarks);
                a.setUpdatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                return;
            }
        }
    }

    public synchronized void withdraw(String appId) { updateStatus(appId, ApplicationStatus.WITHDRAWN, "Withdrawn by student"); }

    public List<Application> getAllApplications() { return new ArrayList<>(applications); }

    public boolean isDuplicate(String studentId, String driveId) {
        return duplicateTracker.contains(studentId + "_" + driveId);
    }

    public Application getById(String id) {
        for (Application a : applications) if (a.getId().equals(id)) return a;
        return null;
    }

    public int getApplicationCount() { return applications.size(); }

    public synchronized void setApplications(List<Application> list) {
        applications.clear(); studentIndex.clear(); duplicateTracker.clear();
        for (Application a : list) {
            applications.add(a);
            duplicateTracker.add(a.getStudentId() + "_" + a.getDriveId());
            studentIndex.computeIfAbsent(a.getStudentId(), k -> new ArrayList<>()).add(a);
        }
    }
}
