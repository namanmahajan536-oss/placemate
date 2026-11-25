package placement.results;

import placement.student.Student;
import java.util.*;

/**
 * Calculates comprehensive placement statistics.
 * Demonstrates HashMap aggregation and sorting for median.
 */
public class PlacementStatistics {
    private int totalStudents, placedStudents, unplacedStudents;
    private double placementPercentage, averagePackage, highestPackage, lowestPackage, medianPackage;
    private Map<String, int[]> branchWisePlacement = new HashMap<>(); // branch -> [total, placed]
    private Map<String, Integer> companyWiseSelection = new HashMap<>();

    public void calculate(List<Student> students, List<PlacementResult> results) {
        totalStudents = students.size();
        placedStudents = 0;
        branchWisePlacement.clear();
        companyWiseSelection.clear();

        // Count by branch
        for (Student s : students) {
            String branch = s.getBranch() != null ? s.getBranch() : "Unknown";
            branchWisePlacement.computeIfAbsent(branch, k -> new int[]{0, 0});
            branchWisePlacement.get(branch)[0]++;
            if (s.isPlaced()) {
                placedStudents++;
                branchWisePlacement.get(branch)[1]++;
            }
        }
        unplacedStudents = totalStudents - placedStudents;
        placementPercentage = totalStudents > 0 ? (placedStudents * 100.0 / totalStudents) : 0;

        // Package statistics from results
        List<Double> packages = new ArrayList<>();
        for (PlacementResult r : results) {
            if (r.getStatus() == PlacementResult.ResultStatus.SELECTED && r.getPackageLPA() > 0) {
                packages.add(r.getPackageLPA());
                companyWiseSelection.merge(r.getCompanyName(), 1, Integer::sum);
            }
        }

        if (!packages.isEmpty()) {
            Collections.sort(packages);
            highestPackage = packages.get(packages.size() - 1);
            lowestPackage = packages.get(0);
            double sum = 0;
            for (double p : packages) sum += p;
            averagePackage = sum / packages.size();
            // Median calculation using sorted list
            int mid = packages.size() / 2;
            medianPackage = packages.size() % 2 == 0 ? (packages.get(mid - 1) + packages.get(mid)) / 2.0 : packages.get(mid);
        }
    }

    // Getters
    public int getTotalStudents() { return totalStudents; }
    public int getPlacedStudents() { return placedStudents; }
    public int getUnplacedStudents() { return unplacedStudents; }
    public double getPlacementPercentage() { return placementPercentage; }
    public double getAveragePackage() { return averagePackage; }
    public double getHighestPackage() { return highestPackage; }
    public double getLowestPackage() { return lowestPackage; }
    public double getMedianPackage() { return medianPackage; }
    public Map<String, int[]> getBranchWiseStats() { return branchWisePlacement; }
    public Map<String, Integer> getCompanyWiseStats() { return companyWiseSelection; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalStudents", totalStudents); map.put("placedStudents", placedStudents);
        map.put("unplacedStudents", unplacedStudents);
        map.put("placementPercentage", String.format("%.1f", placementPercentage));
        map.put("averagePackage", String.format("%.2f", averagePackage));
        map.put("highestPackage", String.format("%.2f", highestPackage));
        map.put("lowestPackage", String.format("%.2f", lowestPackage));
        map.put("medianPackage", String.format("%.2f", medianPackage));
        Map<String, String> branchMap = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> e : branchWisePlacement.entrySet()) {
            branchMap.put(e.getKey(), e.getValue()[1] + "/" + e.getValue()[0]);
        }
        map.put("branchWise", branchMap);
        map.put("companyWise", companyWiseSelection);
        return map;
    }
}
