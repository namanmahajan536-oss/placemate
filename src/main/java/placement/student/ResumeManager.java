package placement.student;

import java.util.*;

/**
 * Manages student resumes. Uses ArrayList for storage.
 */
public class ResumeManager {
    private final List<Resume> resumes = new ArrayList<>();

    public void addResume(Resume resume) {
        resumes.add(resume);
    }

    public void removeResume(String id) {
        resumes.removeIf(r -> r.getId().equals(id));
    }

    public Resume getActiveResume(String studentId) {
        for (Resume r : resumes) {
            if (r.getStudentId().equals(studentId) && r.isActive()) return r;
        }
        return null;
    }

    public List<Resume> getResumesByStudent(String studentId) {
        List<Resume> result = new ArrayList<>();
        for (Resume r : resumes) {
            if (r.getStudentId().equals(studentId)) result.add(r);
        }
        return result;
    }

    public void setActiveResume(String studentId, String resumeId) {
        for (Resume r : resumes) {
            if (r.getStudentId().equals(studentId)) {
                r.setActive(r.getId().equals(resumeId));
            }
        }
    }

    public List<Resume> getAllResumes() { return new ArrayList<>(resumes); }
}
