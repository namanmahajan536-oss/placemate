package placement.application;

import java.util.*;

/**
 * Represents a student's application to a placement drive.
 */
public class Application {
    private String id, studentId, driveId, companyId, companyName;
    private ApplicationStatus status;
    private String appliedAt, updatedAt, remarks, studentName, rollNumber;

    public Application() { this.status = ApplicationStatus.APPLIED; }

    public Application(String id, String studentId, String driveId, String companyId, String companyName) {
        this.id = id; this.studentId = studentId; this.driveId = driveId;
        this.companyId = companyId; this.companyName = companyName;
        this.status = ApplicationStatus.APPLIED;
        this.appliedAt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.updatedAt = this.appliedAt;
    }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getStudentId() { return studentId; } public void setStudentId(String v) { this.studentId = v; }
    public String getDriveId() { return driveId; } public void setDriveId(String v) { this.driveId = v; }
    public String getCompanyId() { return companyId; } public void setCompanyId(String v) { this.companyId = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public ApplicationStatus getStatus() { return status; } public void setStatus(ApplicationStatus v) { this.status = v; }
    public String getAppliedAt() { return appliedAt; } public void setAppliedAt(String v) { this.appliedAt = v; }
    public String getUpdatedAt() { return updatedAt; } public void setUpdatedAt(String v) { this.updatedAt = v; }
    public String getRemarks() { return remarks; } public void setRemarks(String v) { this.remarks = v; }
    public String getStudentName() { return studentName; } public void setStudentName(String v) { this.studentName = v; }
    public String getRollNumber() { return rollNumber; } public void setRollNumber(String v) { this.rollNumber = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("studentId", studentId != null ? studentId : "");
        map.put("driveId", driveId != null ? driveId : ""); map.put("companyId", companyId != null ? companyId : "");
        map.put("companyName", companyName != null ? companyName : "");
        map.put("status", status != null ? status.name() : "APPLIED");
        map.put("appliedAt", appliedAt != null ? appliedAt : "");
        map.put("updatedAt", updatedAt != null ? updatedAt : "");
        map.put("remarks", remarks != null ? remarks : "");
        map.put("studentName", studentName != null ? studentName : "");
        map.put("rollNumber", rollNumber != null ? rollNumber : "");
        return map;
    }

    public static Application fromMap(Map<String, String> map) {
        Application a = new Application();
        a.id = map.getOrDefault("id", ""); a.studentId = map.getOrDefault("studentId", "");
        a.driveId = map.getOrDefault("driveId", ""); a.companyId = map.getOrDefault("companyId", "");
        a.companyName = map.getOrDefault("companyName", "");
        try { a.status = ApplicationStatus.valueOf(map.getOrDefault("status", "APPLIED")); }
        catch (Exception e) { a.status = ApplicationStatus.APPLIED; }
        a.appliedAt = map.getOrDefault("appliedAt", ""); a.updatedAt = map.getOrDefault("updatedAt", "");
        a.remarks = map.getOrDefault("remarks", ""); a.studentName = map.getOrDefault("studentName", "");
        a.rollNumber = map.getOrDefault("rollNumber", "");
        return a;
    }

    @Override
    public String toString() { return id + " | " + companyName + " | " + status.getDisplayName(); }
}
