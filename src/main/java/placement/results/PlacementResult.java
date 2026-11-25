package placement.results;

import java.util.*;

/**
 * Represents the final result of a placement process.
 */
public class PlacementResult {
    public enum ResultStatus { SELECTED, WAITLISTED, REJECTED }

    private String id, studentId, studentName, rollNumber, driveId, companyName, position, resultDate;
    private double packageLPA;
    private ResultStatus status;

    public PlacementResult() {}

    public PlacementResult(String id, String studentId, String driveId, String companyName,
                           double packageLPA, String position) {
        this.id = id; this.studentId = studentId; this.driveId = driveId;
        this.companyName = companyName; this.packageLPA = packageLPA; this.position = position;
        this.status = ResultStatus.SELECTED;
        this.resultDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getStudentId() { return studentId; } public void setStudentId(String v) { this.studentId = v; }
    public String getStudentName() { return studentName; } public void setStudentName(String v) { this.studentName = v; }
    public String getRollNumber() { return rollNumber; } public void setRollNumber(String v) { this.rollNumber = v; }
    public String getDriveId() { return driveId; } public void setDriveId(String v) { this.driveId = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public String getPosition() { return position; } public void setPosition(String v) { this.position = v; }
    public String getResultDate() { return resultDate; } public void setResultDate(String v) { this.resultDate = v; }
    public double getPackageLPA() { return packageLPA; } public void setPackageLPA(double v) { this.packageLPA = v; }
    public ResultStatus getStatus() { return status; } public void setStatus(ResultStatus v) { this.status = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("studentId", studentId != null ? studentId : "");
        map.put("studentName", studentName != null ? studentName : "");
        map.put("rollNumber", rollNumber != null ? rollNumber : "");
        map.put("driveId", driveId != null ? driveId : "");
        map.put("companyName", companyName != null ? companyName : "");
        map.put("packageLPA", String.valueOf(packageLPA)); map.put("position", position != null ? position : "");
        map.put("resultDate", resultDate != null ? resultDate : "");
        map.put("status", status != null ? status.name() : "SELECTED");
        return map;
    }

    public static PlacementResult fromMap(Map<String, String> map) {
        PlacementResult r = new PlacementResult();
        r.id = map.getOrDefault("id", ""); r.studentId = map.getOrDefault("studentId", "");
        r.studentName = map.getOrDefault("studentName", "");
        r.rollNumber = map.getOrDefault("rollNumber", "");
        r.driveId = map.getOrDefault("driveId", ""); r.companyName = map.getOrDefault("companyName", "");
        try { r.packageLPA = Double.parseDouble(map.getOrDefault("packageLPA", "0")); } catch (Exception e) {}
        r.position = map.getOrDefault("position", ""); r.resultDate = map.getOrDefault("resultDate", "");
        try { r.status = ResultStatus.valueOf(map.getOrDefault("status", "SELECTED")); } catch (Exception e) { r.status = ResultStatus.SELECTED; }
        return r;
    }

    @Override
    public String toString() { return companyName + " | " + position + " | " + packageLPA + " LPA | " + status; }
}
