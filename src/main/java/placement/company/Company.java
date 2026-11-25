package placement.company;

import java.util.*;

/**
 * Company entity for placement management.
 */
public class Company {
    private String id;
    private String name;
    private String industry;
    private String website;
    private String description;
    private String location;
    private double minCGPA;
    private List<String> allowedBranches;
    private boolean isActive;
    private String contactEmail;
    private String contactPhone;

    public Company() { this.allowedBranches = new ArrayList<>(); this.isActive = true; }

    public Company(String id, String name, String industry, String location) {
        this(); this.id = id; this.name = name; this.industry = industry; this.location = location;
    }

    // Getters and Setters
    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getIndustry() { return industry; } public void setIndustry(String i) { this.industry = i; }
    public String getWebsite() { return website; } public void setWebsite(String w) { this.website = w; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public String getLocation() { return location; } public void setLocation(String l) { this.location = l; }
    public double getMinCGPA() { return minCGPA; } public void setMinCGPA(double m) { this.minCGPA = m; }
    public List<String> getAllowedBranches() { return allowedBranches; }
    public void setAllowedBranches(List<String> b) { this.allowedBranches = b; }
    public boolean isActive() { return isActive; } public void setActive(boolean a) { this.isActive = a; }
    public String getContactEmail() { return contactEmail; } public void setContactEmail(String e) { this.contactEmail = e; }
    public String getContactPhone() { return contactPhone; } public void setContactPhone(String p) { this.contactPhone = p; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id); map.put("name", name); map.put("industry", industry != null ? industry : "");
        map.put("website", website != null ? website : ""); map.put("description", description != null ? description : "");
        map.put("location", location != null ? location : ""); map.put("minCGPA", String.valueOf(minCGPA));
        map.put("allowedBranches", allowedBranches != null ? String.join(",", allowedBranches) : "");
        map.put("isActive", String.valueOf(isActive));
        map.put("contactEmail", contactEmail != null ? contactEmail : "");
        map.put("contactPhone", contactPhone != null ? contactPhone : "");
        return map;
    }

    public static Company fromMap(Map<String, String> map) {
        Company c = new Company();
        c.id = map.getOrDefault("id", ""); c.name = map.getOrDefault("name", "");
        c.industry = map.getOrDefault("industry", ""); c.website = map.getOrDefault("website", "");
        c.description = map.getOrDefault("description", ""); c.location = map.getOrDefault("location", "");
        try { c.minCGPA = Double.parseDouble(map.getOrDefault("minCGPA", "0")); } catch (Exception e) {}
        String branches = map.getOrDefault("allowedBranches", "");
        c.allowedBranches = branches.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(branches.split(",")));
        c.isActive = Boolean.parseBoolean(map.getOrDefault("isActive", "true"));
        c.contactEmail = map.getOrDefault("contactEmail", "");
        c.contactPhone = map.getOrDefault("contactPhone", "");
        return c;
    }

    @Override
    public String toString() { return name + " (" + industry + ") - " + location; }
}
