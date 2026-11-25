package placement.student;

import java.util.*;

/**
 * Represents a student's resume document.
 */
public class Resume {
    private String id;
    private String studentId;
    private String fileName;
    private String filePath;
    private String uploadDate;
    private boolean isActive;

    public Resume() {}

    public Resume(String id, String studentId, String fileName, String filePath, String uploadDate) {
        this.id = id;
        this.studentId = studentId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("studentId", studentId);
        map.put("fileName", fileName); map.put("filePath", filePath);
        map.put("uploadDate", uploadDate); map.put("isActive", String.valueOf(isActive));
        return map;
    }

    public static Resume fromMap(Map<String, String> map) {
        Resume r = new Resume();
        r.id = map.getOrDefault("id", ""); r.studentId = map.getOrDefault("studentId", "");
        r.fileName = map.getOrDefault("fileName", ""); r.filePath = map.getOrDefault("filePath", "");
        r.uploadDate = map.getOrDefault("uploadDate", "");
        r.isActive = Boolean.parseBoolean(map.getOrDefault("isActive", "true"));
        return r;
    }

    @Override
    public String toString() { return fileName + (isActive ? " [Active]" : " [Inactive]"); }
}
