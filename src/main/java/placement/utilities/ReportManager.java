package placement.utilities;

import placement.results.PlacementStatistics;
import placement.company.Company;
import placement.results.PlacementResult;
import placement.company.PlacementDrive;
import placement.application.Application;
import java.util.*;

/**
 * Generates formatted text reports.
 */
public class ReportManager {

    public static String generatePlacementReport(PlacementStatistics stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════╗\n");
        sb.append("║       PLACEMENT STATISTICS REPORT          ║\n");
        sb.append("╠════════════════════════════════════════════╣\n");
        sb.append(String.format("  Total Students     : %d\n", stats.getTotalStudents()));
        sb.append(String.format("  Placed Students    : %d\n", stats.getPlacedStudents()));
        sb.append(String.format("  Unplaced Students  : %d\n", stats.getUnplacedStudents()));
        sb.append(String.format("  Placement Rate     : %.1f%%\n", stats.getPlacementPercentage()));
        sb.append(String.format("  Average Package    : %.2f LPA\n", stats.getAveragePackage()));
        sb.append(String.format("  Highest Package    : %.2f LPA\n", stats.getHighestPackage()));
        sb.append(String.format("  Lowest Package     : %.2f LPA\n", stats.getLowestPackage()));
        sb.append(String.format("  Median Package     : %.2f LPA\n", stats.getMedianPackage()));
        sb.append("\n  Branch-wise Placement:\n");
        for (Map.Entry<String, int[]> e : stats.getBranchWiseStats().entrySet()) {
            sb.append(String.format("    %-6s: %d/%d placed\n", e.getKey(), e.getValue()[1], e.getValue()[0]));
        }
        sb.append("\n  Company-wise Selection:\n");
        for (Map.Entry<String, Integer> e : stats.getCompanyWiseStats().entrySet()) {
            sb.append(String.format("    %-20s: %d selected\n", e.getKey(), e.getValue()));
        }
        sb.append("╚════════════════════════════════════════════╝");
        return sb.toString();
    }

    public static String generateDriveReport(PlacementDrive drive, List<Application> applications) {
        StringBuilder sb = new StringBuilder();
        sb.append("Drive: ").append(drive.getTitle()).append(" by ").append(drive.getCompanyName()).append("\n");
        sb.append("Status: ").append(drive.getStatus()).append(" | Date: ").append(drive.getDriveDate()).append("\n");
        sb.append("Applications: ").append(applications.size()).append("\n");
        int shortlisted = 0, selected = 0, rejected = 0;
        for (Application a : applications) {
            switch (a.getStatus()) {
                case SHORTLISTED: shortlisted++; break;
                case SELECTED: selected++; break;
                case REJECTED: rejected++; break;
                default: break;
            }
        }
        sb.append("Shortlisted: ").append(shortlisted).append(" | Selected: ").append(selected).append(" | Rejected: ").append(rejected);
        return sb.toString();
    }
}
