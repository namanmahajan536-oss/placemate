package placement.eligibility;

import java.util.*;

/**
 * Defines eligibility criteria for a placement drive.
 */
public class EligibilityCriteria {
    private String id, driveId;
    private double minCGPA;
    private List<String> allowedBranches;
    private int maxActiveBacklogs, maxHistoricalBacklogs, requiredGraduationYear;
    private List<String> requiredSkills;

    public EligibilityCriteria() { allowedBranches = new ArrayList<>(); requiredSkills = new ArrayList<>(); }

    // Getters and Setters
    public String getId() { return id; } public void setId(String v) { this.id = v; }
    public String getDriveId() { return driveId; } public void setDriveId(String v) { this.driveId = v; }
    public double getMinCGPA() { return minCGPA; } public void setMinCGPA(double v) { this.minCGPA = v; }
    public List<String> getAllowedBranches() { return allowedBranches; }
    public void setAllowedBranches(List<String> v) { this.allowedBranches = v; }
    public int getMaxActiveBacklogs() { return maxActiveBacklogs; } public void setMaxActiveBacklogs(int v) { this.maxActiveBacklogs = v; }
    public int getMaxHistoricalBacklogs() { return maxHistoricalBacklogs; } public void setMaxHistoricalBacklogs(int v) { this.maxHistoricalBacklogs = v; }
    public int getRequiredGraduationYear() { return requiredGraduationYear; } public void setRequiredGraduationYear(int v) { this.requiredGraduationYear = v; }
    public List<String> getRequiredSkills() { return requiredSkills; } public void setRequiredSkills(List<String> v) { this.requiredSkills = v; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("driveId", driveId != null ? driveId : "");
        map.put("minCGPA", String.valueOf(minCGPA));
        map.put("allowedBranches", String.join(",", allowedBranches));
        map.put("maxActiveBacklogs", String.valueOf(maxActiveBacklogs));
        map.put("maxHistoricalBacklogs", String.valueOf(maxHistoricalBacklogs));
        map.put("requiredGraduationYear", String.valueOf(requiredGraduationYear));
        map.put("requiredSkills", String.join(",", requiredSkills));
        return map;
    }

    public static EligibilityCriteria fromMap(Map<String, String> map) {
        EligibilityCriteria ec = new EligibilityCriteria();
        ec.id = map.getOrDefault("id", ""); ec.driveId = map.getOrDefault("driveId", "");
        try { ec.minCGPA = Double.parseDouble(map.getOrDefault("minCGPA", "0")); } catch (Exception e) {}
        String br = map.getOrDefault("allowedBranches", "");
        ec.allowedBranches = br.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(br.split(",")));
        try { ec.maxActiveBacklogs = Integer.parseInt(map.getOrDefault("maxActiveBacklogs", "0")); } catch (Exception e) {}
        try { ec.maxHistoricalBacklogs = Integer.parseInt(map.getOrDefault("maxHistoricalBacklogs", "10")); } catch (Exception e) {}
        try { ec.requiredGraduationYear = Integer.parseInt(map.getOrDefault("requiredGraduationYear", "0")); } catch (Exception e) {}
        String sk = map.getOrDefault("requiredSkills", "");
        ec.requiredSkills = sk.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(sk.split(",")));
        return ec;
    }
}
