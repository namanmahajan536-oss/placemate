package placement.company;

import java.util.*;

/**
 * Business logic for company operations.
 */
public class CompanyService {
    private final CompanyManager companyManager;

    public CompanyService(CompanyManager companyManager) { this.companyManager = companyManager; }

    public Company createCompany(String id, String name, String industry, String location,
                                  double minCGPA, List<String> branches) {
        Company c = new Company(id, name, industry, location);
        c.setMinCGPA(minCGPA);
        c.setAllowedBranches(branches);
        companyManager.addCompany(c);
        return c;
    }

    public List<Company> getActiveCompanies() { return companyManager.getActiveCompanies(); }
    public List<Company> searchCompanies(String query) { return companyManager.search(query); }

    public List<Company> getCompaniesByIndustry(String industry) {
        List<Company> result = new ArrayList<>();
        for (Company c : companyManager.getAll()) {
            if (c.getIndustry() != null && c.getIndustry().equalsIgnoreCase(industry)) result.add(c);
        }
        return result;
    }

    public int getCompanyCount() { return companyManager.getCompanyCount(); }

    public void updateCompanyDetails(String id, Map<String, String> fields) {
        Company c = companyManager.getById(id);
        if (c == null) return;
        if (fields.containsKey("name")) c.setName(fields.get("name"));
        if (fields.containsKey("industry")) c.setIndustry(fields.get("industry"));
        if (fields.containsKey("location")) c.setLocation(fields.get("location"));
        if (fields.containsKey("website")) c.setWebsite(fields.get("website"));
        if (fields.containsKey("minCGPA")) try { c.setMinCGPA(Double.parseDouble(fields.get("minCGPA"))); } catch (Exception e) {}
        companyManager.updateCompany(c);
    }

    public CompanyManager getManager() { return companyManager; }
}
