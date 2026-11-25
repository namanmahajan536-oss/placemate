package placement.student;

import java.util.*;

/**
 * CRUD operations for students. Uses ArrayList and demonstrates searching/sorting.
 */
public class StudentManager {
    private final List<Student> students = new ArrayList<>();

    public synchronized void addStudent(Student student) {
        students.add(student);
    }

    public synchronized void removeStudent(String id) {
        students.removeIf(s -> s.getId().equals(id));
    }

    public Student getStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public Student getStudentByRollNumber(String roll) {
        for (Student s : students) {
            if (s.getRollNumber() != null && s.getRollNumber().equalsIgnoreCase(roll)) return s;
        }
        return null;
    }

    public Student getStudentByUsername(String username) {
        for (Student s : students) {
            if (s.getUsername() != null && s.getUsername().equals(username)) return s;
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public synchronized void updateStudent(Student updated) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(updated.getId())) {
                students.set(i, updated);
                return;
            }
        }
    }

    /** Linear search by name, roll number, or branch */
    public List<Student> searchStudents(String query) {
        List<Student> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (Student s : students) {
            if ((s.getName() != null && s.getName().toLowerCase().contains(q)) ||
                (s.getRollNumber() != null && s.getRollNumber().toLowerCase().contains(q)) ||
                (s.getBranch() != null && s.getBranch().toLowerCase().contains(q))) {
                results.add(s);
            }
        }
        return results;
    }

    /** Sort students by CGPA descending using Comparator */
    public List<Student> sortByCGPA() {
        List<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted, (a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));
        return sorted;
    }

    /** Sort students by name using Comparator */
    public List<Student> sortByName() {
        List<Student> sorted = new ArrayList<>(students);
        Collections.sort(sorted, Comparator.comparing(s -> s.getName() != null ? s.getName() : ""));
        return sorted;
    }

    public int getStudentCount() { return students.size(); }

    public synchronized void setStudents(List<Student> studentList) {
        students.clear();
        students.addAll(studentList);
    }
}
