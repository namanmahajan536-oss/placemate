package placement.interview;

import java.util.*;

/**
 * Represents a scheduled interview.
 */
public class Interview {
    public enum InterviewStatus { SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED }
    public enum InterviewResult { PASS, FAIL, PENDING }

    private String id, applicationId, studentId, driveId, companyName;
    private InterviewRound roundType;
    private String scheduledDate, scheduledTime, venue;
    private InterviewStatus status;
    private InterviewResult result;
    private String feedback;

    public Interview() { this.status = InterviewStatus.SCHEDULED; this.result = InterviewResult.PENDING; }

    public Interview(String id, String applicationId, String studentId, String driveId,
                     String companyName, InterviewRound roundType, String scheduledDate,
                     String scheduledTime, String venue) {
        this(); this.id = id; this.applicationId = applicationId; this.studentId = studentId;
        this.driveId = driveId; this.companyName = companyName; this.roundType = roundType;
        this.scheduledDate = scheduledDate; this.scheduledTime = scheduledTime; this.venue = venue;
    }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getApplicationId() { return applicationId; } public void setApplicationId(String v) { this.applicationId = v; }
    public String getStudentId() { return studentId; } public void setStudentId(String v) { this.studentId = v; }
    public String getDriveId() { return driveId; } public void setDriveId(String v) { this.driveId = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public InterviewRound getRoundType() { return roundType; } public void setRoundType(InterviewRound v) { this.roundType = v; }
    public String getScheduledDate() { return scheduledDate; } public void setScheduledDate(String v) { this.scheduledDate = v; }
    public String getScheduledTime() { return scheduledTime; } public void setScheduledTime(String v) { this.scheduledTime = v; }
    public String getVenue() { return venue; } public void setVenue(String v) { this.venue = v; }
    public InterviewStatus getStatus() { return status; } public void setStatus(InterviewStatus v) { this.status = v; }
    public InterviewResult getResult() { return result; } public void setResult(InterviewResult v) { this.result = v; }
    public String getFeedback() { return feedback; } public void setFeedback(String v) { this.feedback = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("applicationId", applicationId != null ? applicationId : "");
        map.put("studentId", studentId != null ? studentId : "");
        map.put("driveId", driveId != null ? driveId : "");
        map.put("companyName", companyName != null ? companyName : "");
        map.put("roundType", roundType != null ? roundType.name() : "APTITUDE_TEST");
        map.put("scheduledDate", scheduledDate != null ? scheduledDate : "");
        map.put("scheduledTime", scheduledTime != null ? scheduledTime : "");
        map.put("venue", venue != null ? venue : "");
        map.put("status", status != null ? status.name() : "SCHEDULED");
        map.put("result", result != null ? result.name() : "PENDING");
        map.put("feedback", feedback != null ? feedback : "");
        return map;
    }

    public static Interview fromMap(Map<String, String> map) {
        Interview i = new Interview();
        i.id = map.getOrDefault("id", ""); i.applicationId = map.getOrDefault("applicationId", "");
        i.studentId = map.getOrDefault("studentId", ""); i.driveId = map.getOrDefault("driveId", "");
        i.companyName = map.getOrDefault("companyName", "");
        try { i.roundType = InterviewRound.valueOf(map.getOrDefault("roundType", "APTITUDE_TEST")); } catch (Exception e) { i.roundType = InterviewRound.APTITUDE_TEST; }
        i.scheduledDate = map.getOrDefault("scheduledDate", "");
        i.scheduledTime = map.getOrDefault("scheduledTime", "");
        i.venue = map.getOrDefault("venue", "");
        try { i.status = InterviewStatus.valueOf(map.getOrDefault("status", "SCHEDULED")); } catch (Exception e) { i.status = InterviewStatus.SCHEDULED; }
        try { i.result = InterviewResult.valueOf(map.getOrDefault("result", "PENDING")); } catch (Exception e) { i.result = InterviewResult.PENDING; }
        i.feedback = map.getOrDefault("feedback", "");
        return i;
    }

    @Override
    public String toString() {
        return roundType.getDisplayName() + " | " + companyName + " | " + scheduledDate + " " + scheduledTime + " | " + status;
    }
}
