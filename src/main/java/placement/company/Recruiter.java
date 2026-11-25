package placement.company;

import placement.authentication.User;
import java.util.*;

/**
 * Recruiter entity extending User. Demonstrates inheritance.
 */
public class Recruiter extends User {
    private String name;
    private String companyId;
    private String designation;
    private String phone;

    public Recruiter() { super(); setRole(Role.RECRUITER); }

    public Recruiter(String id, String username, String passwordHash, String email,
                     String name, String companyId, String designation) {
        super(id, username, passwordHash, Role.RECRUITER, email);
        this.name = name; this.companyId = companyId; this.designation = designation;
    }

    @Override
    public String getDisplayName() { return name + " - " + (designation != null ? designation : "Recruiter"); }

    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getCompanyId() { return companyId; } public void setCompanyId(String c) { this.companyId = c; }
    public String getDesignation() { return designation; } public void setDesignation(String d) { this.designation = d; }
    public String getPhone() { return phone; } public void setPhone(String p) { this.phone = p; }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = super.toMap();
        map.put("name", name != null ? name : "");
        map.put("companyId", companyId != null ? companyId : "");
        map.put("designation", designation != null ? designation : "");
        map.put("phone", phone != null ? phone : "");
        return map;
    }

    public static Recruiter fromMap(Map<String, String> map) {
        Recruiter r = new Recruiter();
        r.setId(map.getOrDefault("id", "")); r.setUsername(map.getOrDefault("username", ""));
        r.setPasswordHash(map.getOrDefault("passwordHash", "")); r.setRole(Role.RECRUITER);
        r.setEmail(map.getOrDefault("email", "")); r.setCreatedAt(map.getOrDefault("createdAt", ""));
        r.name = map.getOrDefault("name", ""); r.companyId = map.getOrDefault("companyId", "");
        r.designation = map.getOrDefault("designation", ""); r.phone = map.getOrDefault("phone", "");
        return r;
    }
}
