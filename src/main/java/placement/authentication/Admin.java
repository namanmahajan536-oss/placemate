package placement.authentication;

import java.util.Map;

/**
 * Admin / TPO entity extending User.
 */
public class Admin extends User {
    private String department;
    private String contactNumber;

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(String id, String username, String passwordHash, String email, String department) {
        super(id, username, passwordHash, Role.ADMIN, email);
        this.department = department;
    }

    @Override
    public String getDisplayName() {
        return "Placement Officer (" + (department != null ? department : "TPO") + ")";
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = super.toMap();
        map.put("department", department != null ? department : "Training & Placement");
        map.put("contactNumber", contactNumber != null ? contactNumber : "");
        return map;
    }

    public static Admin fromMap(Map<String, String> map) {
        Admin a = new Admin();
        a.setId(map.getOrDefault("id", ""));
        a.setUsername(map.getOrDefault("username", ""));
        a.setPasswordHash(map.getOrDefault("passwordHash", ""));
        a.setEmail(map.getOrDefault("email", ""));
        a.setRole(Role.ADMIN);
        a.setDepartment(map.getOrDefault("department", "Training & Placement"));
        a.setContactNumber(map.getOrDefault("contactNumber", ""));
        return a;
    }
}
