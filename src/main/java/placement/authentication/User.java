package placement.authentication;

import placement.exception.InvalidInputException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract base class representing a user in the Placement Management System.
 * Demonstrates abstraction, encapsulation, and polymorphism across user roles.
 */
public abstract class User {

    public enum Role {
        STUDENT,
        ADMIN,
        RECRUITER
    }

    private String id;
    private String username;
    private String passwordHash;
    private Role role;
    private String email;
    private String createdAt;

    protected User() {
        this.createdAt = LocalDateTime.now().toString();
    }

    public User(String id, String username, String passwordHash, Role role, String email) {
        this(id, username, passwordHash, role, email, LocalDateTime.now().toString());
    }

    public User(String id, String username, String passwordHash, Role role, String email, String createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
        this.createdAt = (createdAt != null && !createdAt.trim().isEmpty())
                ? createdAt
                : LocalDateTime.now().toString();
    }

    public abstract String getDisplayName();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("id", id != null ? id : "");
        map.put("username", username != null ? username : "");
        map.put("passwordHash", passwordHash != null ? passwordHash : "");
        map.put("role", role != null ? role.name() : "");
        map.put("email", email != null ? email : "");
        map.put("createdAt", createdAt != null ? createdAt : "");
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return getDisplayName() + " [" + (role != null ? role.name() : "USER") + "]";
    }
}
