package placement.application;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Model class representing a student's application to a placement drive.
 * Demonstrates encapsulation and Map-based serialization for file storage.
 */
public class Application {
    private String id;
    private String studentId;
    private String driveId;
    private String companyId;
    private String companyName;
    private ApplicationStatus status;
    private String appliedAt;
    private String updatedAt;
    private String remarks;
    private String studentName;
    private String rollNumber;

    /**
     * Default constructor initializing status to APPLIED and timestamps.
     */
    public Application() {
        this.status = ApplicationStatus.APPLIED;
        String now = currentTimestamp();
        this.appliedAt = now;
        this.updatedAt = now;
        this.remarks = "";
    }

    /**
     * Creates a new application with core identifiers and defaults status to APPLIED.
     */
    public Application(String id, String studentId, String driveId, String companyId, String companyName) {
        this();
        this.id = id;
        this.studentId = studentId;
        this.driveId = driveId;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    private static String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // ---- Getters and Setters ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status != null ? status : ApplicationStatus.APPLIED;
    }

    public String getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(String appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    /**
     * Converts this Application into a Map for file/JSON serialization.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id != null ? id : "");
        map.put("studentId", studentId != null ? studentId : "");
        map.put("driveId", driveId != null ? driveId : "");
        map.put("companyId", companyId != null ? companyId : "");
        map.put("companyName", companyName != null ? companyName : "");
        map.put("status", status != null ? status.name() : ApplicationStatus.APPLIED.name());
        map.put("appliedAt", appliedAt != null ? appliedAt : "");
        map.put("updatedAt", updatedAt != null ? updatedAt : "");
        map.put("remarks", remarks != null ? remarks : "");
        map.put("studentName", studentName != null ? studentName : "");
        map.put("rollNumber", rollNumber != null ? rollNumber : "");
        return map;
    }

    /**
     * Reconstructs an Application from a serialized String Map.
     */
    public static Application fromMap(Map<String, String> map) {
        Application app = new Application();
        if (map == null) {
            return app;
        }
        app.id = map.getOrDefault("id", "");
        app.studentId = map.getOrDefault("studentId", "");
        app.driveId = map.getOrDefault("driveId", "");
        app.companyId = map.getOrDefault("companyId", "");
        app.companyName = map.getOrDefault("companyName", "");
        app.status = ApplicationStatus.fromString(map.getOrDefault("status", "APPLIED"));
        app.appliedAt = map.getOrDefault("appliedAt", "");
        app.updatedAt = map.getOrDefault("updatedAt", "");
        app.remarks = map.getOrDefault("remarks", "");
        app.studentName = map.getOrDefault("studentName", "");
        app.rollNumber = map.getOrDefault("rollNumber", "");
        return app;
    }

    @Override
    public String toString() {
        String statusLabel = status != null ? status.getDisplayName() : ApplicationStatus.APPLIED.getDisplayName();
        return id + " | " + companyName + " | " + statusLabel;
    }
}
