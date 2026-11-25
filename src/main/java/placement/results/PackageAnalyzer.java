package placement.results;

import placement.student.Student;
import java.util.*;

/**
 * Analyzes package data using sorting and searching algorithms.
 */
public class PackageAnalyzer {

    public double[] getPackageRange(List<PlacementResult> results) {
        List<Double> packages = extractPackages(results);
        if (packages.isEmpty()) return new double[]{0, 0};
        Collections.sort(packages);
        return new double[]{packages.get(0), packages.get(packages.size() - 1)};
    }

    public Map<String, Double> getAverageByBranch(List<PlacementResult> results, List<Student> students) {
        Map<String, String> studentBranch = new HashMap<>();
        for (Student s : students) studentBranch.put(s.getId(), s.getBranch());

        Map<String, double[]> branchTotals = new HashMap<>(); // branch -> [sum, count]
        for (PlacementResult r : results) {
            if (r.getStatus() == PlacementResult.ResultStatus.SELECTED) {
                String branch = studentBranch.getOrDefault(r.getStudentId(), "Unknown");
                branchTotals.computeIfAbsent(branch, k -> new double[]{0, 0});
                branchTotals.get(branch)[0] += r.getPackageLPA();
                branchTotals.get(branch)[1]++;
            }
        }

        Map<String, Double> averages = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> e : branchTotals.entrySet()) {
            averages.put(e.getKey(), e.getValue()[1] > 0 ? e.getValue()[0] / e.getValue()[1] : 0);
        }
        return averages;
    }

    /** Get top N offers by package - uses sorting */
    public List<PlacementResult> getTopOffers(List<PlacementResult> results, int n) {
        List<PlacementResult> sorted = new ArrayList<>(results);
        // Sort by package descending
        Collections.sort(sorted, (a, b) -> Double.compare(b.getPackageLPA(), a.getPackageLPA()));
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    /** Package distribution in ranges */
    public Map<String, Integer> getPackageDistribution(List<PlacementResult> results) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-5 LPA", 0);
        distribution.put("5-10 LPA", 0);
        distribution.put("10-15 LPA", 0);
        distribution.put("15-20 LPA", 0);
        distribution.put("20+ LPA", 0);

        for (PlacementResult r : results) {
            if (r.getStatus() == PlacementResult.ResultStatus.SELECTED) {
                double pkg = r.getPackageLPA();
                if (pkg < 5) distribution.merge("0-5 LPA", 1, Integer::sum);
                else if (pkg < 10) distribution.merge("5-10 LPA", 1, Integer::sum);
                else if (pkg < 15) distribution.merge("10-15 LPA", 1, Integer::sum);
                else if (pkg < 20) distribution.merge("15-20 LPA", 1, Integer::sum);
                else distribution.merge("20+ LPA", 1, Integer::sum);
            }
        }
        return distribution;
    }

    /** Bubble sort implementation for DSA demonstration */
    public void bubbleSortByPackage(List<PlacementResult> results) {
        int n = results.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (results.get(j).getPackageLPA() > results.get(j + 1).getPackageLPA()) {
                    PlacementResult temp = results.get(j);
                    results.set(j, results.get(j + 1));
                    results.set(j + 1, temp);
                }
            }
        }
    }

    private List<Double> extractPackages(List<PlacementResult> results) {
        List<Double> packages = new ArrayList<>();
        for (PlacementResult r : results) {
            if (r.getStatus() == PlacementResult.ResultStatus.SELECTED && r.getPackageLPA() > 0)
                packages.add(r.getPackageLPA());
        }
        return packages;
    }
}
