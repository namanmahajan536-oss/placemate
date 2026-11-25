package placement.interview;

import java.util.*;

/**
 * CRUD for interviews.
 */
public class InterviewManager {
    private final List<Interview> interviews = new ArrayList<>();

    public synchronized void addInterview(Interview interview) { interviews.add(interview); }

    public Interview getById(String id) {
        for (Interview i : interviews) if (i.getId().equals(id)) return i;
        return null;
    }

    public List<Interview> getByStudent(String studentId) {
        List<Interview> result = new ArrayList<>();
        for (Interview i : interviews) if (studentId.equals(i.getStudentId())) result.add(i);
        return result;
    }

    public List<Interview> getByDrive(String driveId) {
        List<Interview> result = new ArrayList<>();
        for (Interview i : interviews) if (driveId.equals(i.getDriveId())) result.add(i);
        return result;
    }

    public List<Interview> getUpcoming() {
        List<Interview> result = new ArrayList<>();
        for (Interview i : interviews) if (i.getStatus() == Interview.InterviewStatus.SCHEDULED) result.add(i);
        return result;
    }

    public synchronized void updateResult(String id, Interview.InterviewResult result, String feedback) {
        Interview i = getById(id);
        if (i != null) { i.setResult(result); i.setFeedback(feedback); i.setStatus(Interview.InterviewStatus.COMPLETED); }
    }

    public synchronized void cancelInterview(String id) {
        Interview i = getById(id);
        if (i != null) i.setStatus(Interview.InterviewStatus.CANCELLED);
    }

    public List<Interview> getAllInterviews() { return new ArrayList<>(interviews); }

    public synchronized void setInterviews(List<Interview> list) { interviews.clear(); interviews.addAll(list); }
}
