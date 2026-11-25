package placement.student;

import placement.authentication.User;
import java.util.*;

/**
 * Student entity extending User. Demonstrates inheritance and polymorphism.
 */
public class Student extends User {
    private String rollNumber;
    private String name;
    private String branch;
    private double cgpa;
    private int graduationYear;
    private int activeBacklogs;
    private int historicalBacklogs;
    private List<Skill> skills;
    private List<String> certifications;
    private List<String> projects;
    private String resumePath;
    private boolean isPlaced;
    private String placedCompany;
    private double packageOffered;

    public Student() {
        super();
        setRole(Role.STUDENT);
        this.skills = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public Student(String id, String username, String passwordHash, String email,
                   String rollNumber, String name, String branch, double cgpa, int graduationYear) {
        super(id, username, passwordHash, Role.STUDENT, email);
        this.rollNumber = rollNumber;
        this.name = name;
        this.branch = branch;
        this.cgpa = cgpa;
        this.graduationYear = graduationYear;
        this.skills = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    @Override
    public String getDisplayName() { return name + " (" + rollNumber + ")"; }

    // Getters and Setters
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int year) { this.graduationYear = year; }
    public int getActiveBacklogs() { return activeBacklogs; }
    public void setActiveBacklogs(int n) { this.activeBacklogs = n; }
    public int getHistoricalBacklogs() { return historicalBacklogs; }
    public void setHistoricalBacklogs(int n) { this.historicalBacklogs = n; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public void addSkill(Skill skill) { this.skills.add(skill); }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> c) { this.certifications = c; }
    public void addCertification(String c) { this.certifications.add(c); }
    public List<String> getProjects() { return projects; }
    public void setProjects(List<String> p) { this.projects = p; }
    public void addProject(String p) { this.projects.add(p); }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String path) { this.resumePath = path; }
    public boolean isPlaced() { return isPlaced; }
    public void setPlaced(boolean placed) { this.isPlaced = placed; }
    public String getPlacedCompany() { return placedCompany; }
    public void setPlacedCompany(String company) { this.placedCompany = company; }
    public double getPackageOffered() { return packageOffered; }
    public void setPackageOffered(double pkg) { this.packageOffered = pkg; }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = super.toMap();
        map.put("rollNumber", rollNumber != null ? rollNumber : "");
        map.put("name", name != null ? name : "");
        map.put("branch", branch != null ? branch : "");
        map.put("cgpa", String.valueOf(cgpa));
        map.put("graduationYear", String.valueOf(graduationYear));
        map.put("activeBacklogs", String.valueOf(activeBacklogs));
        map.put("historicalBacklogs", String.valueOf(historicalBacklogs));
        // Serialize skills as semicolon-separated
        StringBuilder sb = new StringBuilder();
        if (skills != null) {
            for (int i = 0; i < skills.size(); i++) {
                if (i > 0) sb.append(";");
                Skill s = skills.get(i);
                sb.append(s.getName()).append(":").append(s.getProficiencyLevel()).append(":").append(s.getYearsOfExperience());
            }
        }
        map.put("skills", sb.toString());
        map.put("certifications", certifications != null ? String.join(";", certifications) : "");
        map.put("projects", projects != null ? String.join(";", projects) : "");
        map.put("resumePath", resumePath != null ? resumePath : "");
        map.put("isPlaced", String.valueOf(isPlaced));
        map.put("placedCompany", placedCompany != null ? placedCompany : "");
        map.put("packageOffered", String.valueOf(packageOffered));
        return map;
    }

    public static Student fromMap(Map<String, String> map) {
        Student s = new Student();
        s.setId(map.getOrDefault("id", ""));
        s.setUsername(map.getOrDefault("username", ""));
        s.setPasswordHash(map.getOrDefault("passwordHash", ""));
        s.setRole(Role.STUDENT);
        s.setEmail(map.getOrDefault("email", ""));
        s.setCreatedAt(map.getOrDefault("createdAt", ""));
        s.rollNumber = map.getOrDefault("rollNumber", "");
        s.name = map.getOrDefault("name", "");
        s.branch = map.getOrDefault("branch", "");
        try { s.cgpa = Double.parseDouble(map.getOrDefault("cgpa", "0")); } catch (Exception e) { s.cgpa = 0; }
        try { s.graduationYear = Integer.parseInt(map.getOrDefault("graduationYear", "0")); } catch (Exception e) {}
        try { s.activeBacklogs = Integer.parseInt(map.getOrDefault("activeBacklogs", "0")); } catch (Exception e) {}
        try { s.historicalBacklogs = Integer.parseInt(map.getOrDefault("historicalBacklogs", "0")); } catch (Exception e) {}
        // Parse skills
        s.skills = new ArrayList<>();
        String skillsStr = map.getOrDefault("skills", "");
        if (!skillsStr.isEmpty()) {
            for (String sk : skillsStr.split(";")) {
                String[] parts = sk.split(":");
                if (parts.length >= 2) {
                    Skill skill = new Skill();
                    skill.setName(parts[0]);
                    try { skill.setProficiencyLevel(Skill.ProficiencyLevel.valueOf(parts[1])); }
                    catch (Exception e) { skill.setProficiencyLevel(Skill.ProficiencyLevel.BEGINNER); }
                    if (parts.length >= 3) try { skill.setYearsOfExperience(Double.parseDouble(parts[2])); } catch (Exception e) {}
                    s.skills.add(skill);
                }
            }
        }
        s.certifications = new ArrayList<>();
        String certs = map.getOrDefault("certifications", "");
        if (!certs.isEmpty()) { for (String c : certs.split(";")) s.certifications.add(c); }
        s.projects = new ArrayList<>();
        String projs = map.getOrDefault("projects", "");
        if (!projs.isEmpty()) { for (String p : projs.split(";")) s.projects.add(p); }
        s.resumePath = map.getOrDefault("resumePath", "");
        s.isPlaced = Boolean.parseBoolean(map.getOrDefault("isPlaced", "false"));
        s.placedCompany = map.getOrDefault("placedCompany", "");
        try { s.packageOffered = Double.parseDouble(map.getOrDefault("packageOffered", "0")); } catch (Exception e) {}
        return s;
    }
}
