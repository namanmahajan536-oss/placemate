package placement.utilities;

import placement.student.Student;
import placement.company.*;
import placement.application.Application;
import placement.interview.Interview;
import placement.results.PlacementResult;
import placement.authentication.User;
import java.util.*;
import java.io.*;

/**
 * Central data persistence using line-based file format.
 * Singleton pattern. Each record is stored as key=value pairs separated by "|||".
 * Thread-safe with synchronized methods.
 */
public class DataManager {
    private static DataManager instance;
    private String dataDir = "data";

    private DataManager() {
        FileManager.createDirectoryIfNotExists(dataDir);
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public void setDataDir(String dir) {
        this.dataDir = dir;
        FileManager.createDirectoryIfNotExists(dir);
    }

    public String getDataDir() { return dataDir; }

    // ---- Generic save/load using Map serialization ----

    private synchronized void saveRecords(String filename, List<Map<String, String>> records) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> record : records) {
            StringBuilder line = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : record.entrySet()) {
                if (!first) line.append("|||");
                // Escape ||| in values
                String val = entry.getValue() != null ? entry.getValue().replace("|||", "##PIPE##") : "";
                line.append(entry.getKey()).append("=").append(val);
                first = false;
            }
            sb.append(line).append("\n");
        }
        FileManager.writeFile(dataDir + "/" + filename, sb.toString());
    }

    private synchronized List<Map<String, String>> loadRecords(String filename) {
        List<Map<String, String>> records = new ArrayList<>();
        String content = FileManager.readFile(dataDir + "/" + filename);
        if (content == null || content.trim().isEmpty()) return records;
        for (String line : content.split("\n")) {
            if (line.trim().isEmpty()) continue;
            Map<String, String> record = new LinkedHashMap<>();
            for (String pair : line.split("\\|\\|\\|")) {
                int eq = pair.indexOf('=');
                if (eq > 0) {
                    String key = pair.substring(0, eq);
                    String val = pair.substring(eq + 1).replace("##PIPE##", "|||");
                    record.put(key, val);
                }
            }
            if (!record.isEmpty()) records.add(record);
        }
        return records;
    }

    // ---- Students ----
    public synchronized void saveStudents(List<Student> students) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Student s : students) records.add(s.toMap());
        saveRecords("students.dat", records);
    }

    public synchronized List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        for (Map<String, String> map : loadRecords("students.dat")) {
            students.add(Student.fromMap(map));
        }
        return students;
    }

    // ---- Companies ----
    public synchronized void saveCompanies(List<Company> companies) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Company c : companies) records.add(c.toMap());
        saveRecords("companies.dat", records);
    }

    public synchronized List<Company> loadCompanies() {
        List<Company> companies = new ArrayList<>();
        for (Map<String, String> map : loadRecords("companies.dat")) {
            companies.add(Company.fromMap(map));
        }
        return companies;
    }

    // ---- Drives ----
    public synchronized void saveDrives(List<PlacementDrive> drives) {
        List<Map<String, String>> records = new ArrayList<>();
        for (PlacementDrive d : drives) records.add(d.toMap());
        saveRecords("drives.dat", records);
    }

    public synchronized List<PlacementDrive> loadDrives() {
        List<PlacementDrive> drives = new ArrayList<>();
        for (Map<String, String> map : loadRecords("drives.dat")) {
            drives.add(PlacementDrive.fromMap(map));
        }
        return drives;
    }

    // ---- Applications ----
    public synchronized void saveApplications(List<Application> applications) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Application a : applications) records.add(a.toMap());
        saveRecords("applications.dat", records);
    }

    public synchronized List<Application> loadApplications() {
        List<Application> applications = new ArrayList<>();
        for (Map<String, String> map : loadRecords("applications.dat")) {
            applications.add(Application.fromMap(map));
        }
        return applications;
    }

    // ---- Interviews ----
    public synchronized void saveInterviews(List<Interview> interviews) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Interview i : interviews) records.add(i.toMap());
        saveRecords("interviews.dat", records);
    }

    public synchronized List<Interview> loadInterviews() {
        List<Interview> interviews = new ArrayList<>();
        for (Map<String, String> map : loadRecords("interviews.dat")) {
            interviews.add(Interview.fromMap(map));
        }
        return interviews;
    }

    // ---- Results ----
    public synchronized void saveResults(List<PlacementResult> results) {
        List<Map<String, String>> records = new ArrayList<>();
        for (PlacementResult r : results) records.add(r.toMap());
        saveRecords("results.dat", records);
    }

    public synchronized List<PlacementResult> loadResults() {
        List<PlacementResult> results = new ArrayList<>();
        for (Map<String, String> map : loadRecords("results.dat")) {
            results.add(PlacementResult.fromMap(map));
        }
        return results;
    }

    // ---- Recruiters ----
    public synchronized void saveRecruiters(List<Recruiter> recruiters) {
        List<Map<String, String>> records = new ArrayList<>();
        for (Recruiter r : recruiters) records.add(r.toMap());
        saveRecords("recruiters.dat", records);
    }

    public synchronized List<Recruiter> loadRecruiters() {
        List<Recruiter> recruiters = new ArrayList<>();
        for (Map<String, String> map : loadRecords("recruiters.dat")) {
            recruiters.add(Recruiter.fromMap(map));
        }
        return recruiters;
    }

    /** Check if data files exist (for first-run detection) */
    public boolean hasData() {
        return FileManager.fileExists(dataDir + "/students.dat");
    }
}
