package placement.application;

import java.util.*;

/**
 * Manages shortlisting and rejection of candidates.
 */
public class ShortlistingManager {
    private final ApplicationManager applicationManager;

    public ShortlistingManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public void shortlistCandidate(String appId) {
        applicationManager.updateStatus(appId, ApplicationStatus.SHORTLISTED, "Shortlisted by recruiter");
    }

    public void rejectCandidate(String appId, String reason) {
        applicationManager.updateStatus(appId, ApplicationStatus.REJECTED, reason);
    }

    public void bulkShortlist(List<String> appIds) {
        for (String id : appIds) shortlistCandidate(id);
    }

    public List<Application> getShortlistedForDrive(String driveId) {
        List<Application> result = new ArrayList<>();
        for (Application a : applicationManager.getByDrive(driveId)) {
            if (a.getStatus() == ApplicationStatus.SHORTLISTED) result.add(a);
        }
        return result;
    }

    public List<Application> getRejectedForDrive(String driveId) {
        List<Application> result = new ArrayList<>();
        for (Application a : applicationManager.getByDrive(driveId)) {
            if (a.getStatus() == ApplicationStatus.REJECTED) result.add(a);
        }
        return result;
    }
}
