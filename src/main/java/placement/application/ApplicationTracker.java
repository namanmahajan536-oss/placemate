package placement.application;

import java.util.*;

/**
 * Tracks application progress and provides status views.
 */
public class ApplicationTracker {
    private final ApplicationManager applicationManager;

    public ApplicationTracker(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public ApplicationStatus getCurrentStatus(String appId) {
        Application app = applicationManager.getById(appId);
        return app != null ? app.getStatus() : null;
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationManager.getByStatus(status);
    }

    public List<Application> getActiveApplications(String studentId) {
        List<Application> active = new ArrayList<>();
        for (Application a : applicationManager.getByStudent(studentId)) {
            if (a.getStatus() != ApplicationStatus.REJECTED &&
                a.getStatus() != ApplicationStatus.WITHDRAWN &&
                a.getStatus() != ApplicationStatus.SELECTED) {
                active.add(a);
            }
        }
        return active;
    }

    public int getApplicationCountByStatus(ApplicationStatus status) {
        return applicationManager.getByStatus(status).size();
    }
}
