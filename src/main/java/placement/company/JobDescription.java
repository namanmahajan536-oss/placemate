package placement.company;

import java.util.*;

/**
 * Represents a job posting by a company.
 */
public class JobDescription {
    public enum JobType { FULL_TIME, INTERNSHIP, PPO }

    private String id, companyId, driveId, title, description, location, postedDate;
    private List<String> requirements;
    private double packageLPA;
    private JobType jobType;
    private int openPositions;

    public JobDescription() { this.requirements = new ArrayList<>(); }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getCompanyId() { return companyId; } public void setCompanyId(String v) { this.companyId = v; }
    public String getDriveId() { return driveId; } public void setDriveId(String v) { this.driveId = v; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getLocation() { return location; } public void setLocation(String v) { this.location = v; }
    public String getPostedDate() { return postedDate; } public void setPostedDate(String v) { this.postedDate = v; }
    public List<String> getRequirements() { return requirements; }
    public void setRequirements(List<String> v) { this.requirements = v; }
    public double getPackageLPA() { return packageLPA; } public void setPackageLPA(double v) { this.packageLPA = v; }
    public JobType getJobType() { return jobType; } public void setJobType(JobType v) { this.jobType = v; }
    public int getOpenPositions() { return openPositions; } public void setOpenPositions(int v) { this.openPositions = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("companyId", companyId != null ? companyId : "");
        map.put("driveId", driveId != null ? driveId : ""); map.put("title", title != null ? title : "");
        map.put("description", description != null ? description : "");
        map.put("location", location != null ? location : "");
        map.put("requirements", requirements != null ? String.join(",", requirements) : "");
        map.put("packageLPA", String.valueOf(packageLPA));
        map.put("jobType", jobType != null ? jobType.name() : "FULL_TIME");
        map.put("openPositions", String.valueOf(openPositions));
        map.put("postedDate", postedDate != null ? postedDate : "");
        return map;
    }

    public static JobDescription fromMap(Map<String, String> map) {
        JobDescription j = new JobDescription();
        j.id = map.getOrDefault("id", ""); j.companyId = map.getOrDefault("companyId", "");
        j.driveId = map.getOrDefault("driveId", ""); j.title = map.getOrDefault("title", "");
        j.description = map.getOrDefault("description", ""); j.location = map.getOrDefault("location", "");
        String reqs = map.getOrDefault("requirements", "");
        j.requirements = reqs.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(reqs.split(",")));
        try { j.packageLPA = Double.parseDouble(map.getOrDefault("packageLPA", "0")); } catch (Exception e) {}
        try { j.jobType = JobType.valueOf(map.getOrDefault("jobType", "FULL_TIME")); } catch (Exception e) { j.jobType = JobType.FULL_TIME; }
        try { j.openPositions = Integer.parseInt(map.getOrDefault("openPositions", "0")); } catch (Exception e) {}
        j.postedDate = map.getOrDefault("postedDate", "");
        return j;
    }

    @Override
    public String toString() { return title + " | " + packageLPA + " LPA | " + jobType; }
}
