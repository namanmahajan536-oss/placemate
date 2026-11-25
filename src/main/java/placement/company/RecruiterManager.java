package placement.company;

import java.util.*;

/**
 * CRUD for recruiters.
 */
public class RecruiterManager {
    private final List<Recruiter> recruiters = new ArrayList<>();

    public synchronized void addRecruiter(Recruiter r) { recruiters.add(r); }
    public Recruiter getById(String id) {
        for (Recruiter r : recruiters) if (r.getId().equals(id)) return r;
        return null;
    }
    public Recruiter getByUsername(String username) {
        for (Recruiter r : recruiters) if (r.getUsername() != null && r.getUsername().equals(username)) return r;
        return null;
    }
    public List<Recruiter> getByCompanyId(String companyId) {
        List<Recruiter> result = new ArrayList<>();
        for (Recruiter r : recruiters) if (companyId.equals(r.getCompanyId())) result.add(r);
        return result;
    }
    public List<Recruiter> getAllRecruiters() { return new ArrayList<>(recruiters); }
    public synchronized void updateRecruiter(Recruiter updated) {
        for (int i = 0; i < recruiters.size(); i++) {
            if (recruiters.get(i).getId().equals(updated.getId())) { recruiters.set(i, updated); return; }
        }
    }
    public synchronized void removeRecruiter(String id) { recruiters.removeIf(r -> r.getId().equals(id)); }
    public synchronized void setRecruiters(List<Recruiter> list) { recruiters.clear(); recruiters.addAll(list); }
}
