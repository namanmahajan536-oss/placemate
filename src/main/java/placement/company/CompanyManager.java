package placement.company;

import java.util.*;

/**
 * CRUD for companies with sorting using Comparator.
 */
public class CompanyManager {
    private final List<Company> companies = new ArrayList<>();

    public synchronized void addCompany(Company company) { companies.add(company); }

    public Company getById(String id) {
        for (Company c : companies) if (c.getId().equals(id)) return c;
        return null;
    }

    public List<Company> getAll() { return new ArrayList<>(companies); }

    public List<Company> search(String query) {
        List<Company> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (Company c : companies) {
            if ((c.getName() != null && c.getName().toLowerCase().contains(q)) ||
                (c.getIndustry() != null && c.getIndustry().toLowerCase().contains(q)))
                results.add(c);
        }
        return results;
    }

    public synchronized void updateCompany(Company updated) {
        for (int i = 0; i < companies.size(); i++) {
            if (companies.get(i).getId().equals(updated.getId())) {
                companies.set(i, updated); return;
            }
        }
    }

    public synchronized void removeCompany(String id) { companies.removeIf(c -> c.getId().equals(id)); }

    public List<Company> getActiveCompanies() {
        List<Company> result = new ArrayList<>();
        for (Company c : companies) if (c.isActive()) result.add(c);
        return result;
    }

    public List<Company> sortByName() {
        List<Company> sorted = new ArrayList<>(companies);
        Collections.sort(sorted, Comparator.comparing(c -> c.getName() != null ? c.getName() : ""));
        return sorted;
    }

    public int getCompanyCount() { return companies.size(); }

    public synchronized void setCompanies(List<Company> list) { companies.clear(); companies.addAll(list); }
}
