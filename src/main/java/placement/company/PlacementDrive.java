package placement.company;

import java.util.*;

/**
 * Represents a placement drive organized by a company.
 */
public class PlacementDrive {
    public enum DriveStatus { UPCOMING, ONGOING, COMPLETED, CANCELLED }

    private String id, companyId, companyName, title, description;
    private String driveDate, lastDateToApply, eligibilityCriteriaId;
    private DriveStatus status;
    private List<String> jobDescriptionIds;
    private int registeredStudentCount, selectedStudentCount;
    private double minCGPA;
    private List<String> allowedBranches;
    private int maxActiveBacklogs;
    private int requiredGraduationYear;

    public PlacementDrive() {
        this.jobDescriptionIds = new ArrayList<>();
        this.allowedBranches = new ArrayList<>();
        this.status = DriveStatus.UPCOMING;
    }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getCompanyId() { return companyId; } public void setCompanyId(String v) { this.companyId = v; }
    public String getCompanyName() { return companyName; } public void setCompanyName(String v) { this.companyName = v; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getDriveDate() { return driveDate; } public void setDriveDate(String v) { this.driveDate = v; }
    public String getLastDateToApply() { return lastDateToApply; } public void setLastDateToApply(String v) { this.lastDateToApply = v; }
    public String getEligibilityCriteriaId() { return eligibilityCriteriaId; }
    public void setEligibilityCriteriaId(String v) { this.eligibilityCriteriaId = v; }
    public DriveStatus getStatus() { return status; } public void setStatus(DriveStatus v) { this.status = v; }
    public List<String> getJobDescriptionIds() { return jobDescriptionIds; }
    public void setJobDescriptionIds(List<String> v) { this.jobDescriptionIds = v; }
    public int getRegisteredStudentCount() { return registeredStudentCount; }
    public void setRegisteredStudentCount(int v) { this.registeredStudentCount = v; }
    public int getSelectedStudentCount() { return selectedStudentCount; }
    public void setSelectedStudentCount(int v) { this.selectedStudentCount = v; }
    public double getMinCGPA() { return minCGPA; } public void setMinCGPA(double v) { this.minCGPA = v; }
    public List<String> getAllowedBranches() { return allowedBranches; }
    public void setAllowedBranches(List<String> v) { this.allowedBranches = v; }
    public int getMaxActiveBacklogs() { return maxActiveBacklogs; } public void setMaxActiveBacklogs(int v) { this.maxActiveBacklogs = v; }
    public int getRequiredGraduationYear() { return requiredGraduationYear; }
    public void setRequiredGraduationYear(int v) { this.requiredGraduationYear = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("companyId", companyId != null ? companyId : "");
        map.put("companyName", companyName != null ? companyName : "");
        map.put("title", title != null ? title : ""); map.put("description", description != null ? description : "");
        map.put("driveDate", driveDate != null ? driveDate : "");
        map.put("lastDateToApply", lastDateToApply != null ? lastDateToApply : "");
        map.put("eligibilityCriteriaId", eligibilityCriteriaId != null ? eligibilityCriteriaId : "");
        map.put("status", status != null ? status.name() : "UPCOMING");
        map.put("jobDescriptionIds", jobDescriptionIds != null ? String.join(",", jobDescriptionIds) : "");
        map.put("registeredStudentCount", String.valueOf(registeredStudentCount));
        map.put("selectedStudentCount", String.valueOf(selectedStudentCount));
        map.put("minCGPA", String.valueOf(minCGPA));
        map.put("allowedBranches", allowedBranches != null ? String.join(",", allowedBranches) : "");
        map.put("maxActiveBacklogs", String.valueOf(maxActiveBacklogs));
        map.put("requiredGraduationYear", String.valueOf(requiredGraduationYear));
        return map;
    }

    public static PlacementDrive fromMap(Map<String, String> map) {
        PlacementDrive d = new PlacementDrive();
        d.id = map.getOrDefault("id", ""); d.companyId = map.getOrDefault("companyId", "");
        d.companyName = map.getOrDefault("companyName", "");
        d.title = map.getOrDefault("title", ""); d.description = map.getOrDefault("description", "");
        d.driveDate = map.getOrDefault("driveDate", ""); d.lastDateToApply = map.getOrDefault("lastDateToApply", "");
        d.eligibilityCriteriaId = map.getOrDefault("eligibilityCriteriaId", "");
        try { d.status = DriveStatus.valueOf(map.getOrDefault("status", "UPCOMING")); } catch (Exception e) { d.status = DriveStatus.UPCOMING; }
        String jids = map.getOrDefault("jobDescriptionIds", "");
        d.jobDescriptionIds = jids.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(jids.split(",")));
        try { d.registeredStudentCount = Integer.parseInt(map.getOrDefault("registeredStudentCount", "0")); } catch (Exception e) {}
        try { d.selectedStudentCount = Integer.parseInt(map.getOrDefault("selectedStudentCount", "0")); } catch (Exception e) {}
        try { d.minCGPA = Double.parseDouble(map.getOrDefault("minCGPA", "0")); } catch (Exception e) {}
        String br = map.getOrDefault("allowedBranches", "");
        d.allowedBranches = br.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(br.split(",")));
        try { d.maxActiveBacklogs = Integer.parseInt(map.getOrDefault("maxActiveBacklogs", "0")); } catch (Exception e) {}
        try { d.requiredGraduationYear = Integer.parseInt(map.getOrDefault("requiredGraduationYear", "0")); } catch (Exception e) {}
        return d;
    }

    @Override
    public String toString() { return title + " by " + companyName + " (" + status + ") - " + driveDate; }
}
