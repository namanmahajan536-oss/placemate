package placement.interview;

import java.util.*;

/**
 * Validates interview scheduling data.
 */
public class InterviewValidator {
    public List<String> validateSchedule(Interview interview) {
        List<String> errors = new ArrayList<>();
        if (interview.getStudentId() == null || interview.getStudentId().isEmpty()) errors.add("Student ID required");
        if (interview.getScheduledDate() == null || interview.getScheduledDate().isEmpty()) errors.add("Date required");
        if (interview.getScheduledTime() == null || interview.getScheduledTime().isEmpty()) errors.add("Time required");
        if (interview.getVenue() == null || interview.getVenue().isEmpty()) errors.add("Venue required");
        return errors;
    }

    public boolean checkTimeConflict(String studentId, String date, String time, List<Interview> existing) {
        for (Interview i : existing) {
            if (i.getStudentId().equals(studentId) && i.getScheduledDate().equals(date) &&
                i.getScheduledTime().equals(time) && i.getStatus() == Interview.InterviewStatus.SCHEDULED)
                return true;
        }
        return false;
    }
}
