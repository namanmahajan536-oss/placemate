package placement.eligibility;

import java.util.*;

/**
 * Model class representing the eligibility criteria for a placement drive.
 * Demonstrates encapsulation and Map-based serialization for file storage.
 */
public class EligibilityCriteria {
    private String id;
    private String driveId;
    private double minCGPA;
    private List<String> allowedBranches;
    private int maxActiveBacklogs;
    private int maxHistoricalBacklogs;
    private int requiredGraduationYear;
    private List<String> requiredSkills;

    /**
     * Default constructor initializing empty lists to avoid null pointer issues.
     */
    public EligibilityCriteria() {
        this.allowedBranches = new ArrayList<>();
        this.requiredSkills = new ArrayList<>();
        this.maxHistoricalBacklogs = 10;
    }

    /**
     * Full constructor for creating eligibility criteria programmatically.
     */
    public EligibilityCriteria(String id, String driveId, double minCGPA,
                               List<String> allowedBranches, int maxActiveBacklogs,
                               int maxHistoricalBacklogs, int requiredGraduationYear,
                               List<String> requiredSkills) {
        this.id = id;
        this.driveId = driveId;
        this.minCGPA = minCGPA;
        this.allowedBranches = allowedBranches != null ? new ArrayList<>(allowedBranches) : new ArrayList<>();
        this.maxActiveBacklogs = maxActiveBacklogs;
        this.maxHistoricalBacklogs = maxHistoricalBacklogs;
        this.requiredGraduationYear = requiredGraduationYear;
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }

    // ---- Getters and Setters (Encapsulation) ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public double getMinCGPA() {
        return minCGPA;
    }

    public void setMinCGPA(double minCGPA) {
        this.minCGPA = minCGPA;
    }

    public List<String> getAllowedBranches() {
        return allowedBranches;
    }

    public void setAllowedBranches(List<String> allowedBranches) {
        this.allowedBranches = allowedBranches != null ? new ArrayList<>(allowedBranches) : new ArrayList<>();
    }

    public int getMaxActiveBacklogs() {
        return maxActiveBacklogs;
    }

    public void setMaxActiveBacklogs(int maxActiveBacklogs) {
        this.maxActiveBacklogs = maxActiveBacklogs;
    }

    public int getMaxHistoricalBacklogs() {
        return maxHistoricalBacklogs;
    }

    public void setMaxHistoricalBacklogs(int maxHistoricalBacklogs) {
        this.maxHistoricalBacklogs = maxHistoricalBacklogs;
    }

    public int getRequiredGraduationYear() {
        return requiredGraduationYear;
    }

    public void setRequiredGraduationYear(int requiredGraduationYear) {
        this.requiredGraduationYear = requiredGraduationYear;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
    }

    /**
     * Adds an allowed branch if it is not already present (case-insensitive).
     */
    public void addAllowedBranch(String branch) {
        if (branch == null || branch.trim().isEmpty()) {
            return;
        }
        String trimmed = branch.trim();
        for (String existing : allowedBranches) {
            if (existing.equalsIgnoreCase(trimmed)) {
                return;
            }
        }
        allowedBranches.add(trimmed);
    }

    /**
     * Adds a required skill if it is not already present (case-insensitive).
     */
    public void addRequiredSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return;
        }
        String trimmed = skill.trim();
        for (String existing : requiredSkills) {
            if (existing.equalsIgnoreCase(trimmed)) {
                return;
            }
        }
        requiredSkills.add(trimmed);
    }

    /**
     * Converts this EligibilityCriteria object into a flat String Map for JSON/file persistence.
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id != null ? id : "");
        map.put("driveId", driveId != null ? driveId : "");
        map.put("minCGPA", String.valueOf(minCGPA));
        map.put("allowedBranches", allowedBranches != null ? String.join(",", allowedBranches) : "");
        map.put("maxActiveBacklogs", String.valueOf(maxActiveBacklogs));
        map.put("maxHistoricalBacklogs", String.valueOf(maxHistoricalBacklogs));
        map.put("requiredGraduationYear", String.valueOf(requiredGraduationYear));
        map.put("requiredSkills", requiredSkills != null ? String.join(",", requiredSkills) : "");
        return map;
    }

    /**
     * Reconstructs an EligibilityCriteria object from a serialized String Map.
     */
    public static EligibilityCriteria fromMap(Map<String, String> map) {
        EligibilityCriteria criteria = new EligibilityCriteria();
        if (map == null) {
            return criteria;
        }
        criteria.id = map.getOrDefault("id", "");
        criteria.driveId = map.getOrDefault("driveId", "");
        try {
            criteria.minCGPA = Double.parseDouble(map.getOrDefault("minCGPA", "0"));
        } catch (NumberFormatException ignored) {
            criteria.minCGPA = 0.0;
        }
        String branches = map.getOrDefault("allowedBranches", "");
        if (!branches.trim().isEmpty()) {
            for (String b : branches.split(",")) {
                if (!b.trim().isEmpty()) {
                    criteria.allowedBranches.add(b.trim());
                }
            }
        }
        try {
            criteria.maxActiveBacklogs = Integer.parseInt(map.getOrDefault("maxActiveBacklogs", "0"));
        } catch (NumberFormatException ignored) {
            criteria.maxActiveBacklogs = 0;
        }
        try {
            criteria.maxHistoricalBacklogs = Integer.parseInt(map.getOrDefault("maxHistoricalBacklogs", "10"));
        } catch (NumberFormatException ignored) {
            criteria.maxHistoricalBacklogs = 10;
        }
        try {
            criteria.requiredGraduationYear = Integer.parseInt(map.getOrDefault("requiredGraduationYear", "0"));
        } catch (NumberFormatException ignored) {
            criteria.requiredGraduationYear = 0;
        }
        String skills = map.getOrDefault("requiredSkills", "");
        if (!skills.trim().isEmpty()) {
            for (String s : skills.split(",")) {
                if (!s.trim().isEmpty()) {
                    criteria.requiredSkills.add(s.trim());
                }
            }
        }
        return criteria;
    }

    @Override
    public String toString() {
        return String.format("EligibilityCriteria[id=%s, driveId=%s, minCGPA=%.2f, branches=%s, maxActive=%d, maxHist=%d, gradYear=%d]",
                id, driveId, minCGPA, allowedBranches, maxActiveBacklogs, maxHistoricalBacklogs, requiredGraduationYear);
    }
}
