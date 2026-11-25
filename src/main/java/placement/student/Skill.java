package placement.student;

import java.util.*;

/**
 * Represents a skill with proficiency level.
 * Demonstrates encapsulation and enum usage.
 */
public class Skill {
    public enum ProficiencyLevel { BEGINNER, INTERMEDIATE, ADVANCED }

    private String name;
    private ProficiencyLevel proficiencyLevel;
    private double yearsOfExperience;

    public Skill() {}

    public Skill(String name, ProficiencyLevel proficiencyLevel, double yearsOfExperience) {
        this.name = name;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(ProficiencyLevel level) { this.proficiencyLevel = level; }
    public double getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(double years) { this.yearsOfExperience = years; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("proficiencyLevel", proficiencyLevel != null ? proficiencyLevel.name() : "BEGINNER");
        map.put("yearsOfExperience", String.valueOf(yearsOfExperience));
        return map;
    }

    public static Skill fromMap(Map<String, String> map) {
        Skill skill = new Skill();
        skill.setName(map.getOrDefault("name", ""));
        try { skill.setProficiencyLevel(ProficiencyLevel.valueOf(map.getOrDefault("proficiencyLevel", "BEGINNER"))); }
        catch (Exception e) { skill.setProficiencyLevel(ProficiencyLevel.BEGINNER); }
        try { skill.setYearsOfExperience(Double.parseDouble(map.getOrDefault("yearsOfExperience", "0"))); }
        catch (Exception e) { skill.setYearsOfExperience(0); }
        return skill;
    }

    @Override
    public String toString() {
        return name + " (" + proficiencyLevel + ", " + yearsOfExperience + " yrs)";
    }
}
