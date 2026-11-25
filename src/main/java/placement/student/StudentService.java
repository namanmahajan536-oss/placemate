package placement.student;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Business logic layer for student operations.
 */
public class StudentService {
    private final StudentManager studentManager;

    public StudentService(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public Student registerStudent(String id, String username, String passwordHash, String email,
                                   String rollNumber, String name, String branch, double cgpa, int graduationYear) {
        Student student = new Student(id, username, passwordHash, email, rollNumber, name, branch, cgpa, graduationYear);
        studentManager.addStudent(student);
        return student;
    }

    public void updateProfile(String id, Map<String, String> fields) {
        Student s = studentManager.getStudentById(id);
        if (s == null) return;
        if (fields.containsKey("cgpa")) try { s.setCgpa(Double.parseDouble(fields.get("cgpa"))); } catch (Exception e) {}
        if (fields.containsKey("branch")) s.setBranch(fields.get("branch"));
        if (fields.containsKey("activeBacklogs")) try { s.setActiveBacklogs(Integer.parseInt(fields.get("activeBacklogs"))); } catch (Exception e) {}
        if (fields.containsKey("historicalBacklogs")) try { s.setHistoricalBacklogs(Integer.parseInt(fields.get("historicalBacklogs"))); } catch (Exception e) {}
        if (fields.containsKey("name")) s.setName(fields.get("name"));
        if (fields.containsKey("email")) s.setEmail(fields.get("email"));
        studentManager.updateStudent(s);
    }

    public StudentProfile getProfile(String id) {
        Student s = studentManager.getStudentById(id);
        return s != null ? new StudentProfile(s) : null;
    }

    public List<Student> searchByBranch(String branch) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (s.getBranch() != null && s.getBranch().equalsIgnoreCase(branch)) result.add(s);
        }
        return result;
    }

    public List<Student> searchByCGPA(double minCGPA) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (s.getCgpa() >= minCGPA) result.add(s);
        }
        return result;
    }

    public List<Student> getPlacedStudents() {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (s.isPlaced()) result.add(s);
        }
        return result;
    }

    public List<Student> getUnplacedStudents() {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (!s.isPlaced()) result.add(s);
        }
        return result;
    }

    public List<Student> getStudentsByGraduationYear(int year) {
        List<Student> result = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (s.getGraduationYear() == year) result.add(s);
        }
        return result;
    }

    public List<Student> getTopStudentsByCGPA(int n) {
        List<Student> sorted = studentManager.sortByCGPA();
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    public int getStudentCount() { return studentManager.getStudentCount(); }

    public double getPlacementRate() {
        int total = studentManager.getStudentCount();
        if (total == 0) return 0;
        int placed = 0;
        for (Student s : studentManager.getAllStudents()) if (s.isPlaced()) placed++;
        return (placed * 100.0) / total;
    }

    public StudentManager getManager() { return studentManager; }
}
