package placement.interview;

import java.util.*;

/**
 * Schedules interviews with conflict detection using TreeMap for chronological ordering.
 */
public class InterviewScheduler {
    private final TreeMap<String, List<Interview>> dateIndex = new TreeMap<>();

    public boolean hasConflict(String studentId, String date, String time, List<Interview> allInterviews) {
        for (Interview i : allInterviews) {
            if (i.getStudentId().equals(studentId) && i.getScheduledDate().equals(date) &&
                i.getScheduledTime().equals(time) && i.getStatus() == Interview.InterviewStatus.SCHEDULED) {
                return true;
            }
        }
        return false;
    }

    public void scheduleInterview(Interview interview) {
        dateIndex.computeIfAbsent(interview.getScheduledDate(), k -> new ArrayList<>()).add(interview);
    }

    public List<Interview> getScheduleForDate(String date) {
        return dateIndex.getOrDefault(date, new ArrayList<>());
    }

    public void reschedule(Interview interview, String newDate, String newTime) {
        // Remove from old date
        List<Interview> oldList = dateIndex.get(interview.getScheduledDate());
        if (oldList != null) oldList.remove(interview);
        // Update and add to new date
        interview.setScheduledDate(newDate);
        interview.setScheduledTime(newTime);
        interview.setStatus(Interview.InterviewStatus.RESCHEDULED);
        dateIndex.computeIfAbsent(newDate, k -> new ArrayList<>()).add(interview);
    }

    public Map<String, List<Interview>> getAllSchedules() { return new TreeMap<>(dateIndex); }
}
