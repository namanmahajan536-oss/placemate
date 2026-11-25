package placement.application;

import java.util.*;

/**
 * Tracks application progress, timelines, and active/status-filtered application views.
 */
public class ApplicationTracker {
    private final ApplicationManager applicationManager;

    public ApplicationTracker(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    /**
     * Returns the current ApplicationStatus for the given application ID, or null if not found.
     */
    public ApplicationStatus getCurrentStatus(String appId) {
        Application app = applicationManager.getById(appId);
        return app != null ? app.getStatus() : null;
    }

    /**
     * Returns the chronological timeline of status updates for an application.
     */
    public List<String> getStatusTimeline(String appId) {
        Application app = applicationManager.getById(appId);
        if (app == null) {
            return new ArrayList<>();
        }
        List<String> recorded = applicationManager.getStatusTimeline(appId);
        if (!recorded.isEmpty()) {
            return recorded;
        }
        // Fallback synthesis if timeline events were not recorded separately
        List<String> timeline = new ArrayList<>();
        timeline.add(String.format("[%s] %s - Application submitted",
                app.getAppliedAt() != null ? app.getAppliedAt() : "",
                ApplicationStatus.APPLIED.getDisplayName()));
        if (app.getStatus() != ApplicationStatus.APPLIED) {
            String remarkSuffix = (app.getRemarks() != null && !app.getRemarks().isEmpty())
                    ? " - " + app.getRemarks()
                    : "";
            timeline.add(String.format("[%s] %s%s",
                    app.getUpdatedAt() != null ? app.getUpdatedAt() : "",
                    app.getStatus().getDisplayName(),
                    remarkSuffix));
        }
        return timeline;
    }

    /**
     * Returns all applications matching the specified status.
     */
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationManager.getByStatus(status);
    }

    /**
     * Returns all active (non-terminal) applications for a student.
     */
    public List<Application> getActiveApplications(String studentId) {
        List<Application> active = new ArrayList<>();
        for (Application app : applicationManager.getByStudent(studentId)) {
            if (app.getStatus() != null && !app.getStatus().isTerminal()) {
                active.add(app);
            }
        }
        return active;
    }

    /**
     * Returns the count of applications currently in the given status.
     */
    public int getApplicationCountByStatus(ApplicationStatus status) {
        return applicationManager.getByStatus(status).size();
    }
}
