package placement.authentication;

import placement.exception.DuplicateRecordException;
import placement.exception.InvalidInputException;
import placement.student.Student;
import placement.company.Recruiter;

import java.util.*;

/**
 * Registration service for user accounts.
 */
public class RegistrationService {
    private final List<User> registeredUsers = new ArrayList<>();

    public synchronized Student registerStudent(String id, String username, String plainPassword,
                                                String email, String roll, String name, String branch,
                                                double cgpa, int year) {
        validateUniqueUsername(username);
        String hash = PasswordManager.hashPassword(plainPassword);
        Student s = new Student(id, username, hash, email, roll, name, branch, cgpa, year);
        registeredUsers.add(s);
        return s;
    }

    public synchronized Recruiter registerRecruiter(String id, String username, String plainPassword,
                                                    String email, String name, String companyId, String designation) {
        validateUniqueUsername(username);
        String hash = PasswordManager.hashPassword(plainPassword);
        Recruiter r = new Recruiter(id, username, hash, email, name, companyId, designation);
        registeredUsers.add(r);
        return r;
    }

    public synchronized Admin registerAdmin(String id, String username, String plainPassword,
                                            String email, String department) {
        validateUniqueUsername(username);
        String hash = PasswordManager.hashPassword(plainPassword);
        Admin a = new Admin(id, username, hash, email, department);
        registeredUsers.add(a);
        return a;
    }

    private void validateUniqueUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        for (User u : registeredUsers) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username.trim())) {
                throw new DuplicateRecordException("Username '" + username + "' is already registered.");
            }
        }
    }

    public List<User> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }
}
