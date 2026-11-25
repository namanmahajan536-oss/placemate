package placement.application;

import java.util.*;

/**
 * Manages shortlisting and rejection decisions for placement drive applications.
 */
public class ShortlistingManager {
    private final ApplicationManager applicationManager;

    public ShortlistingManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    /**
     * Marks a single candidate application as SHORTLISTED.
     */
    public void shortlistCandidate(String appId) {
        if (appId == null || appId.trim().isEmpty()) {
            return;
        }
        applicationManager.updateStatus(appId, ApplicationStatus.SHORTLISTED, "Shortlisted by recruiter");
    }

    /**
     * Marks a candidate application as REJECTED with an explanatory reason.
     */
    public void rejectCandidate(String appId, String reason) {
        if (appId == null || appId.trim().isEmpty()) {
            return;
        }
        String finalReason = (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "Not selected";
        applicationManager.updateStatus(appId, ApplicationStatus.REJECTED, finalReason);
    }

    /**
     * Shortlists multiple candidate applications in a single batch.
     */
    public void bulkShortlist(List<String> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            return;
        }
        for (String id : appIds) {
            shortlistCandidate(id);
        }
    }

    /**
     * Returns all SHORTLISTED applications for the specified placement drive.
     */
    public List<Application> getShortlistedForDrive(String driveId) {
        return filterByDriveAndStatus(driveId, ApplicationStatus.SHORTLISTED);
    }

    /**
     * Returns all REJECTED applications for the specified placement drive.
     */
    public List<Application> getRejectedForDrive(String driveId) {
        return filterByDriveAndStatus(driveId, ApplicationStatus.REJECTED);
    }

    /**
     * Returns applications for the drive that are still APPLIED or UNDER_REVIEW.
     */
    public List<Application> getPendingForDrive(String driveId) {
        List<Application> pending = new ArrayList<>();
        for (Application app : applicationManager.getByDrive(driveId)) {
            if (app.getStatus() == ApplicationStatus.APPLIED || app.getStatus() == ApplicationStatus.UNDER_REVIEW) {
                pending.add(app);
            }
        }
        return pending;
    }

    private List<Application> filterByDriveAndStatus(String driveId, ApplicationStatus status) {
        List<Application> result = new ArrayList<>();
        for (Application app : applicationManager.getByDrive(driveId)) {
            if (app.getStatus() == status) {
                result.add(app);
            }
        }
        return result;
    }
}
