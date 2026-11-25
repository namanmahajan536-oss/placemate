package placement.utilities;

import placement.student.Student;
import placement.company.PlacementDrive;
import placement.results.PlacementResult;
import java.util.*;

/**
 * Sorting utilities demonstrating Comparator and bubble sort.
 */
public class SortManager {

    /** Sort students by CGPA descending */
    public static void sortStudentsByCGPA(List<Student> students) {
        Collections.sort(students, (a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));
    }

    /** Sort students by name alphabetically */
    public static void sortStudentsByName(List<Student> students) {
        Collections.sort(students, Comparator.comparing(s -> s.getName() != null ? s.getName() : ""));
    }

    /** Sort drives by date */
    public static void sortDrivesByDate(List<PlacementDrive> drives) {
        Collections.sort(drives, Comparator.comparing(d -> d.getDriveDate() != null ? d.getDriveDate() : ""));
    }

    /** Sort results by package descending */
    public static void sortResultsByPackage(List<PlacementResult> results) {
        Collections.sort(results, (a, b) -> Double.compare(b.getPackageLPA(), a.getPackageLPA()));
    }

    /** Bubble sort demonstration (ascending by CGPA) */
    public static void bubbleSortByCGPA(List<Student> students) {
        int n = students.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (students.get(j).getCgpa() > students.get(j + 1).getCgpa()) {
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }
    }

    /** Generic sort by map field */
    public static void sortByField(List<Map<String, String>> data, String field) {
        data.sort(Comparator.comparing(m -> m.getOrDefault(field, "")));
    }
}
