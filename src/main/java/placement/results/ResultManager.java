package placement.results;

import java.util.*;

/**
 * CRUD for placement results.
 */
public class ResultManager {
    private final List<PlacementResult> results = new ArrayList<>();

    public synchronized void addResult(PlacementResult result) { results.add(result); }

    public PlacementResult getById(String id) {
        for (PlacementResult r : results) if (r.getId().equals(id)) return r;
        return null;
    }

    public List<PlacementResult> getByStudent(String studentId) {
        List<PlacementResult> list = new ArrayList<>();
        for (PlacementResult r : results) if (studentId.equals(r.getStudentId())) list.add(r);
        return list;
    }

    public List<PlacementResult> getByDrive(String driveId) {
        List<PlacementResult> list = new ArrayList<>();
        for (PlacementResult r : results) if (driveId.equals(r.getDriveId())) list.add(r);
        return list;
    }

    public List<PlacementResult> getByCompany(String company) {
        List<PlacementResult> list = new ArrayList<>();
        for (PlacementResult r : results) if (company.equalsIgnoreCase(r.getCompanyName())) list.add(r);
        return list;
    }

    public List<PlacementResult> getSelectedStudents() {
        List<PlacementResult> list = new ArrayList<>();
        for (PlacementResult r : results) if (r.getStatus() == PlacementResult.ResultStatus.SELECTED) list.add(r);
        return list;
    }

    public synchronized void removeResult(String id) {
        results.removeIf(r -> r.getId().equals(id));
    }

    public List<PlacementResult> getAllResults() { return new ArrayList<>(results); }

    public synchronized void updateResult(String id, PlacementResult.ResultStatus status) {
        PlacementResult r = getById(id);
        if (r != null) r.setStatus(status);
    }

    public synchronized void setResults(List<PlacementResult> list) { results.clear(); results.addAll(list); }
}
