package placement.application;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Manages CRUD operations, indexing, duplicate prevention, and review queueing for Applications.
 * Uses ArrayList for ordered storage, HashMap for O(1) student lookup,
 * HashSet for duplicate detection, and Queue for FIFO review processing.
 */
public class ApplicationManager {
    private final List<Application> applications = new ArrayList<>();
    private final HashMap<String, List<Application>> studentIndex = new HashMap<>();
    private final HashSet<String> duplicateTracker = new HashSet<>(); // key = studentId + "_" + driveId
    private final Queue<Application> reviewQueue = new LinkedList<>();
    private final HashMap<String, List<String>> timelineMap = new HashMap<>();

    private static String buildDuplicateKey(String studentId, String driveId) {
        return (studentId != null ? studentId : "") + "_" + (driveId != null ? driveId : "");
    }

    private static String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Submits and indexes a new application if it is not a duplicate for the same student and drive.
     *
     * @param app the Application to store
     * @return true if added successfully, false if null or duplicate
     */
    public synchronized boolean submitApplication(Application app) {
        if (app == null || app.getStudentId() == null || app.getDriveId() == null) {
            return false;
        }
        String key = buildDuplicateKey(app.getStudentId(), app.getDriveId());
        if (duplicateTracker.contains(key)) {
            return false;
        }
        if (app.getStatus() == null) {
            app.setStatus(ApplicationStatus.APPLIED);
        }
        if (app.getAppliedAt() == null || app.getAppliedAt().isEmpty()) {
            app.setAppliedAt(currentTimestamp());
        }
        if (app.getUpdatedAt() == null || app.getUpdatedAt().isEmpty()) {
            app.setUpdatedAt(app.getAppliedAt());
        }

        applications.add(app);
        duplicateTracker.add(key);
        studentIndex.computeIfAbsent(app.getStudentId(), k -> new ArrayList<>()).add(app);
        if (app.getStatus() == ApplicationStatus.APPLIED || app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
            reviewQueue.offer(app);
        }
        recordTimelineEvent(app.getId(), app.getAppliedAt(), app.getStatus(), "Application submitted");
        return true;
    }

    /**
     * Returns all applications belonging to a specific student (defensive copy).
     */
    public List<Application> getByStudent(String studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }
        List<Application> indexed = studentIndex.get(studentId);
        return indexed != null ? new ArrayList<>(indexed) : new ArrayList<>();
    }

    /**
     * Returns all applications submitted for a given placement drive.
     */
    public List<Application> getByDrive(String driveId) {
        List<Application> result = new ArrayList<>();
        if (driveId == null) {
            return result;
        }
        for (Application app : applications) {
            if (driveId.equals(app.getDriveId())) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Returns all applications currently matching the specified status.
     */
    public List<Application> getByStatus(ApplicationStatus status) {
        List<Application> result = new ArrayList<>();
        if (status == null) {
            return result;
        }
        for (Application app : applications) {
            if (app.getStatus() == status) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Updates the status, remarks, and updatedAt timestamp of an existing application.
     */
    public synchronized void updateStatus(String appId, ApplicationStatus newStatus, String remarks) {
        if (appId == null || newStatus == null) {
            return;
        }
        for (Application app : applications) {
            if (appId.equals(app.getId())) {
                String now = currentTimestamp();
                app.setStatus(newStatus);
                app.setRemarks(remarks != null ? remarks : "");
                app.setUpdatedAt(now);
                recordTimelineEvent(app.getId(), now, newStatus, app.getRemarks());
                return;
            }
        }
    }

    /**
     * Marks an application as WITHDRAWN by the student.
     */
    public synchronized void withdraw(String appId) {
        updateStatus(appId, ApplicationStatus.WITHDRAWN, "Withdrawn by student");
    }

    /**
     * Returns a copy of all stored applications.
     */
    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }

    /**
     * Checks in O(1) time whether the student has already applied to the drive.
     */
    public boolean isDuplicate(String studentId, String driveId) {
        return duplicateTracker.contains(buildDuplicateKey(studentId, driveId));
    }

    /**
     * Finds a single application by its unique ID.
     */
    public Application getById(String id) {
        if (id == null) {
            return null;
        }
        for (Application app : applications) {
            if (id.equals(app.getId())) {
                return app;
            }
        }
        return null;
    }

    /**
     * Polls the next pending application from the FIFO review queue.
     */
    public synchronized Application pollNextForReview() {
        return reviewQueue.poll();
    }

    /**
     * Searches applications by partial company name (case-insensitive).
     */
    public List<Application> searchByCompanyName(String query) {
        List<Application> result = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return result;
        }
        String normalized = query.trim().toLowerCase();
        for (Application app : applications) {
            if (app.getCompanyName() != null && app.getCompanyName().toLowerCase().contains(normalized)) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Returns all applications sorted by appliedAt date descending (newest first).
     */
    public List<Application> sortByAppliedDateDescending() {
        List<Application> sorted = new ArrayList<>(applications);
        Collections.sort(sorted, (a, b) -> {
            String da = a.getAppliedAt() != null ? a.getAppliedAt() : "";
            String db = b.getAppliedAt() != null ? b.getAppliedAt() : "";
            return db.compareTo(da);
        });
        return sorted;
    }

    /**
     * Returns the recorded status timeline events for an application.
     */
    public List<String> getStatusTimeline(String appId) {
        List<String> events = timelineMap.get(appId);
        return events != null ? new ArrayList<>(events) : new ArrayList<>();
    }

    private void recordTimelineEvent(String appId, String timestamp, ApplicationStatus status, String note) {
        if (appId == null || appId.isEmpty()) {
            return;
        }
        String entry = String.format("[%s] %s%s",
                timestamp != null ? timestamp : "",
                status != null ? status.getDisplayName() : "",
                (note != null && !note.trim().isEmpty()) ? " - " + note.trim() : "");
        timelineMap.computeIfAbsent(appId, k -> new ArrayList<>()).add(entry);
    }

    public int getApplicationCount() {
        return applications.size();
    }

    /**
     * Replaces all stored applications (typically called on startup after loading from disk)
     * and rebuilds the HashMap index, HashSet duplicate tracker, Queue, and timeline.
     */
    public synchronized void setApplications(List<Application> list) {
        applications.clear();
        studentIndex.clear();
        duplicateTracker.clear();
        reviewQueue.clear();
        timelineMap.clear();

        if (list == null) {
            return;
        }
        for (Application app : list) {
            if (app == null) {
                continue;
            }
            applications.add(app);
            duplicateTracker.add(buildDuplicateKey(app.getStudentId(), app.getDriveId()));
            if (app.getStudentId() != null) {
                studentIndex.computeIfAbsent(app.getStudentId(), k -> new ArrayList<>()).add(app);
            }
            if (app.getStatus() == ApplicationStatus.APPLIED || app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                reviewQueue.offer(app);
            }
            recordTimelineEvent(app.getId(), app.getAppliedAt(), ApplicationStatus.APPLIED, "Application submitted");
            if (app.getStatus() != null && app.getStatus() != ApplicationStatus.APPLIED) {
                recordTimelineEvent(app.getId(), app.getUpdatedAt(), app.getStatus(), app.getRemarks());
            }
        }
    }
}
