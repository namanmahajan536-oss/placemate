package placement.company;

import java.util.*;

/**
 * CRUD for placement drives. Uses Queue for processing order.
 */
public class PlacementDriveManager {
    private final List<PlacementDrive> drives = new ArrayList<>();
    private final Queue<PlacementDrive> processingQueue = new LinkedList<>();

    public synchronized void createDrive(PlacementDrive drive) {
        drives.add(drive);
        if (drive.getStatus() == PlacementDrive.DriveStatus.UPCOMING) {
            processingQueue.offer(drive);
        }
    }

    public PlacementDrive getDriveById(String id) {
        for (PlacementDrive d : drives) if (d.getId().equals(id)) return d;
        return null;
    }

    public List<PlacementDrive> getActiveDrives() {
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : drives)
            if (d.getStatus() == PlacementDrive.DriveStatus.UPCOMING || d.getStatus() == PlacementDrive.DriveStatus.ONGOING)
                result.add(d);
        return result;
    }

    public List<PlacementDrive> getUpcomingDrives() {
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : drives) if (d.getStatus() == PlacementDrive.DriveStatus.UPCOMING) result.add(d);
        return result;
    }

    public List<PlacementDrive> getCompletedDrives() {
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : drives) if (d.getStatus() == PlacementDrive.DriveStatus.COMPLETED) result.add(d);
        return result;
    }

    public List<PlacementDrive> getDrivesByCompany(String companyId) {
        List<PlacementDrive> result = new ArrayList<>();
        for (PlacementDrive d : drives) if (companyId.equals(d.getCompanyId())) result.add(d);
        return result;
    }

    public synchronized void updateDriveStatus(String id, PlacementDrive.DriveStatus status) {
        PlacementDrive d = getDriveById(id);
        if (d != null) d.setStatus(status);
    }

    public List<PlacementDrive> getAllDrives() { return new ArrayList<>(drives); }

    /** Retrieves next drive to process from Queue (FIFO) */
    public PlacementDrive getNextDriveToProcess() { return processingQueue.poll(); }

    public List<PlacementDrive> sortByDate() {
        List<PlacementDrive> sorted = new ArrayList<>(drives);
        Collections.sort(sorted, Comparator.comparing(d -> d.getDriveDate() != null ? d.getDriveDate() : ""));
        return sorted;
    }

    public int getDriveCount() { return drives.size(); }

    public synchronized void setDrives(List<PlacementDrive> list) {
        drives.clear(); drives.addAll(list);
        processingQueue.clear();
        for (PlacementDrive d : list) {
            if (d.getStatus() == PlacementDrive.DriveStatus.UPCOMING) processingQueue.offer(d);
        }
    }
}
