package placement.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import placement.authentication.*;
import placement.student.*;
import placement.company.*;
import placement.eligibility.*;
import placement.application.*;
import placement.interview.*;
import placement.results.*;
import placement.utilities.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Embedded HTTP REST API and Static File Server for PLACEMENT system.
 * Zero external dependencies, uses com.sun.net.httpserver.HttpServer.
 */
public final class HttpApiServer {
    public static final int DEFAULT_PORT = 8080;

    private final int port;
    private final HttpServer server;
    private final Path webRoot;

    private final DataManager dataManager;
    private final StudentManager studentManager;
    private final StudentService studentService;
    private final CompanyManager companyManager;
    private final CompanyService companyService;
    private final PlacementDriveManager driveManager;
    private final RecruiterManager recruiterManager;
    private final ApplicationManager applicationManager;
    private final ApplicationService applicationService;
    private final InterviewManager interviewManager;
    private final InterviewScheduler interviewScheduler;
    private final ResultManager resultManager;
    private final PlacementStatistics placementStatistics;
    private final PackageAnalyzer packageAnalyzer;
    private final NotificationManager notificationManager;
    private final EligibilityChecker eligibilityChecker;
    private final EligibilityService eligibilityService;
    private final AuthenticationService authService;

    public HttpApiServer(int port) throws IOException {
        this.port = port;
        this.webRoot = Paths.get("web").toAbsolutePath().normalize();

        this.dataManager = DataManager.getInstance();
        this.dataManager.setDataDir("data");

        this.studentManager = new StudentManager();
        this.studentService = new StudentService(studentManager);
        this.companyManager = new CompanyManager();
        this.companyService = new CompanyService(companyManager);
        this.driveManager = new PlacementDriveManager();
        this.recruiterManager = new RecruiterManager();
        this.applicationManager = new ApplicationManager();
        this.applicationService = new ApplicationService(applicationManager, studentManager, driveManager);
        this.interviewManager = new InterviewManager();
        this.interviewScheduler = new InterviewScheduler();
        this.resultManager = new ResultManager();
        this.placementStatistics = new PlacementStatistics();
        this.packageAnalyzer = new PackageAnalyzer();
        this.notificationManager = new NotificationManager();
        this.eligibilityChecker = new EligibilityChecker();
        this.eligibilityService = new EligibilityService(eligibilityChecker, studentManager, driveManager);
        this.authService = new AuthenticationService();

        loadInitialData();

        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newCachedThreadPool());

        registerRoutes();
    }

    private void loadInitialData() {
        List<Student> sList = dataManager.loadStudents();
        if (!sList.isEmpty()) studentManager.setStudents(sList);

        List<Company> cList = dataManager.loadCompanies();
        if (!cList.isEmpty()) companyManager.setCompanies(cList);

        List<PlacementDrive> dList = dataManager.loadDrives();
        if (!dList.isEmpty()) driveManager.setDrives(dList);

        List<Recruiter> rList = dataManager.loadRecruiters();
        if (!rList.isEmpty()) recruiterManager.setRecruiters(rList);

        List<Application> aList = dataManager.loadApplications();
        if (!aList.isEmpty()) applicationManager.setApplications(aList);

        List<Interview> iList = dataManager.loadInterviews();
        if (!iList.isEmpty()) interviewManager.setInterviews(iList);

        List<PlacementResult> resList = dataManager.loadResults();
        if (!resList.isEmpty()) resultManager.setResults(resList);

        seedIfEmpty();
    }

    private void seedIfEmpty() {
        if (studentManager.getStudentCount() == 0) {
            Student s1 = new Student("STU001", "student1", PasswordManager.hashPassword("pass123"),
                    "rahul@campus.edu", "CS2024001", "Rahul Sharma", "CSE", 8.5, 2025);
            s1.addSkill(new Skill("Java", Skill.ProficiencyLevel.ADVANCED, 2.5));
            s1.addSkill(new Skill("DSA", Skill.ProficiencyLevel.ADVANCED, 2.0));
            s1.addSkill(new Skill("Spring Boot", Skill.ProficiencyLevel.INTERMEDIATE, 1.0));
            studentManager.addStudent(s1);

            Student s2 = new Student("STU002", "student2", PasswordManager.hashPassword("pass123"),
                    "priya@campus.edu", "EC2024001", "Priya Patel", "ECE", 7.8, 2025);
            s2.addSkill(new Skill("Embedded C", Skill.ProficiencyLevel.ADVANCED, 2.0));
            s2.addSkill(new Skill("Python", Skill.ProficiencyLevel.INTERMEDIATE, 1.5));
            studentManager.addStudent(s2);

            Student s3 = new Student("STU003", "student3", PasswordManager.hashPassword("pass123"),
                    "amit@campus.edu", "ME2024001", "Amit Kumar", "ME", 6.8, 2025);
            s3.addSkill(new Skill("AutoCAD", Skill.ProficiencyLevel.ADVANCED, 2.0));
            studentManager.addStudent(s3);
        }

        if (companyManager.getCompanyCount() == 0) {
            Company c1 = new Company("COMP001", "TechCorp Solutions", "Information Technology", "Bangalore");
            c1.setMinCGPA(7.0);
            c1.setAllowedBranches(Arrays.asList("CSE", "ECE", "IT"));
            c1.setContactEmail("careers@techcorp.com");
            c1.setWebsite("https://techcorp.example.com");
            c1.setDescription("Leading enterprise software and cloud engineering firm.");
            companyManager.addCompany(c1);

            Company c2 = new Company("COMP002", "InnovateTech Labs", "Software & FinTech", "Hyderabad");
            c2.setMinCGPA(7.5);
            c2.setAllowedBranches(Arrays.asList("CSE", "IT"));
            c2.setContactEmail("hr@innovatetech.com");
            c2.setWebsite("https://innovatetech.example.com");
            c2.setDescription("High-performance quantitative and financial software systems.");
            companyManager.addCompany(c2);
        }

        if (driveManager.getDriveCount() == 0) {
            PlacementDrive d1 = new PlacementDrive();
            d1.setId("DRV001");
            d1.setCompanyId("COMP001");
            d1.setCompanyName("TechCorp Solutions");
            d1.setTitle("Graduate Software Engineer 2025");
            d1.setDescription("Hiring Java, Cloud and Web developers.");
            d1.setDriveDate("2025-04-15");
            d1.setLastDateToApply("2025-04-10");
            d1.setMinCGPA(7.0);
            d1.setAllowedBranches(Arrays.asList("CSE", "ECE", "IT"));
            d1.setMaxActiveBacklogs(0);
            d1.setRequiredGraduationYear(2025);
            d1.setStatus(PlacementDrive.DriveStatus.UPCOMING);
            driveManager.createDrive(d1);
        }

        if (recruiterManager.getAllRecruiters().isEmpty()) {
            Recruiter r1 = new Recruiter("REC001", "recruiter1", PasswordManager.hashPassword("pass123"),
                    "recruiter@techcorp.com", "Vikram Malhotra", "COMP001", "Head of Campus Talent");
            recruiterManager.addRecruiter(r1);
        }

        notificationManager.addNotification("ALL", "Welcome to PLACEMENT Campus Placement Portal!");
        notificationManager.addNotification("STU001", "Campus Drive announced by TechCorp Solutions!");
        persistAll();
    }

    private void persistAll() {
        dataManager.saveStudents(studentManager.getAllStudents());
        dataManager.saveCompanies(companyManager.getAll());
        dataManager.saveDrives(driveManager.getAllDrives());
        dataManager.saveRecruiters(recruiterManager.getAllRecruiters());
        dataManager.saveApplications(applicationManager.getAllApplications());
        dataManager.saveInterviews(interviewManager.getAllInterviews());
        dataManager.saveResults(resultManager.getAllResults());
    }

    private void registerRoutes() {
        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/auth/register", new RegisterHandler());
        server.createContext("/api/students", new StudentsHandler());
        server.createContext("/api/companies", new CompaniesHandler());
        server.createContext("/api/drives", new DrivesHandler());
        server.createContext("/api/eligibility", new EligibilityHandler());
        server.createContext("/api/applications", new ApplicationsHandler());
        server.createContext("/api/interviews", new InterviewsHandler());
        server.createContext("/api/results", new ResultsHandler());
        server.createContext("/api/statistics", new StatisticsHandler());
        server.createContext("/api/notifications", new NotificationsHandler());
        server.createContext("/", new StaticFileHandler());
    }

    public void start() {
        server.start();
        System.out.println("=============================================================");
        System.out.println(" PLACEMENT Web Server running at: http://localhost:" + port);
        System.out.println(" Dashboard Web UI: http://localhost:" + port + "/");
        System.out.println(" REST API Health : http://localhost:" + port + "/api/status");
        System.out.println("=============================================================");
    }

    public void stop() {
        server.stop(0);
        System.out.println("PLACEMENT Web Server stopped.");
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        try {
            HttpApiServer s = new HttpApiServer(port);
            s.start();
            System.out.println("Press Ctrl+C to terminate.");
            // Keep main thread alive so daemon server stays running
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        } catch (Exception e) {
            System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // API HANDLERS
    // ----------------------------------------------------
    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("status", "ok");
            data.put("app", "PLACEMENT - Smart Campus Placement Management System");
            data.put("version", "1.0.0");
            data.put("studentsCount", studentManager.getStudentCount());
            data.put("drivesCount", driveManager.getDriveCount());
            data.put("companiesCount", companyManager.getCompanyCount());
            sendJsonResponse(exchange, 200, toJson(data));
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            String body = readRequestBody(exchange);
            Map<String, String> req = parseSimpleJson(body);
            String username = req.get("username");
            String password = req.get("password");
            String roleReq = req.getOrDefault("role", "STUDENT");

            if ("ADMIN".equalsIgnoreCase(roleReq) || "admin".equalsIgnoreCase(username)) {
                if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("success", true);
                    res.put("role", "ADMIN");
                    res.put("name", "Placement Officer");
                    res.put("username", "admin");
                    res.put("token", UUID.randomUUID().toString());
                    sendJsonResponse(exchange, 200, toJson(res));
                    return;
                }
            }

            if ("RECRUITER".equalsIgnoreCase(roleReq)) {
                Recruiter r = recruiterManager.getByUsername(username);
                if (r != null && PasswordManager.verifyPassword(password, r.getPasswordHash())) {
                    Map<String, Object> res = new LinkedHashMap<>();
                    res.put("success", true);
                    res.put("role", "RECRUITER");
                    res.put("id", r.getId());
                    res.put("name", r.getName());
                    res.put("companyId", r.getCompanyId());
                    res.put("token", UUID.randomUUID().toString());
                    sendJsonResponse(exchange, 200, toJson(res));
                    return;
                }
            }

            Student s = studentManager.getStudentByUsername(username);
            if (s != null && PasswordManager.verifyPassword(password, s.getPasswordHash())) {
                Map<String, Object> res = new LinkedHashMap<>();
                res.put("success", true);
                res.put("role", "STUDENT");
                res.put("id", s.getId());
                res.put("name", s.getName());
                res.put("rollNumber", s.getRollNumber());
                res.put("branch", s.getBranch());
                res.put("cgpa", s.getCgpa());
                res.put("token", UUID.randomUUID().toString());
                sendJsonResponse(exchange, 200, toJson(res));
                return;
            }

            sendJsonResponse(exchange, 401, "{\"success\":false,\"error\":\"Invalid credentials.\"}");
        }
    }

    private class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            String body = readRequestBody(exchange);
            Map<String, String> req = parseSimpleJson(body);

            String username = req.get("username");
            if (studentManager.getStudentByUsername(username) != null) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Username already exists\"}");
                return;
            }
            String id = "STU" + String.format("%03d", studentManager.getStudentCount() + 1);
            Student s = new Student(id, username, PasswordManager.hashPassword(req.getOrDefault("password", "pass123")),
                    req.getOrDefault("email", ""), req.getOrDefault("rollNumber", ""), req.getOrDefault("name", ""),
                    req.getOrDefault("branch", "CSE"),
                    Double.parseDouble(req.getOrDefault("cgpa", "7.0")),
                    Integer.parseInt(req.getOrDefault("graduationYear", "2025")));
            studentManager.addStudent(s);
            persistAll();

            Map<String, Object> res = new LinkedHashMap<>();
            res.put("success", true);
            res.put("studentId", id);
            sendJsonResponse(exchange, 200, toJson(res));
        }
    }

    private class StudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> qMap = parseQuery(query);
                if (qMap.containsKey("id")) {
                    Student s = studentManager.getStudentById(qMap.get("id"));
                    sendJsonResponse(exchange, s != null ? 200 : 404, s != null ? toJson(s.toMap()) : "{\"error\":\"Not found\"}");
                } else {
                    List<Map<String, String>> list = new ArrayList<>();
                    for (Student s : studentManager.getAllStudents()) list.add(s.toMap());
                    sendJsonResponse(exchange, 200, toJson(list));
                }
            } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String id = req.get("id");
                studentService.updateProfile(id, req);
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true}");
            }
        }
    }

    private class CompaniesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Map<String, String>> list = new ArrayList<>();
                for (Company c : companyManager.getAll()) list.add(c.toMap());
                sendJsonResponse(exchange, 200, toJson(list));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String id = "COMP" + String.format("%03d", companyManager.getCompanyCount() + 1);
                Company c = new Company(id, req.get("name"), req.get("industry"), req.get("location"));
                c.setMinCGPA(Double.parseDouble(req.getOrDefault("minCGPA", "6.5")));
                if (req.containsKey("allowedBranches")) {
                    c.setAllowedBranches(Arrays.asList(req.get("allowedBranches").split(",")));
                }
                c.setDescription(req.getOrDefault("description", ""));
                c.setWebsite(req.getOrDefault("website", ""));
                companyManager.addCompany(c);
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":\"" + id + "\"}");
            }
        }
    }

    private class DrivesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Map<String, String>> list = new ArrayList<>();
                for (PlacementDrive d : driveManager.getAllDrives()) list.add(d.toMap());
                sendJsonResponse(exchange, 200, toJson(list));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String id = "DRV" + String.format("%03d", driveManager.getDriveCount() + 1);
                PlacementDrive d = new PlacementDrive();
                d.setId(id);
                d.setCompanyId(req.get("companyId"));
                d.setCompanyName(req.get("companyName"));
                d.setTitle(req.get("title"));
                d.setDescription(req.getOrDefault("description", ""));
                d.setDriveDate(req.getOrDefault("driveDate", "2025-05-01"));
                d.setLastDateToApply(req.getOrDefault("lastDateToApply", "2025-04-20"));
                d.setMinCGPA(Double.parseDouble(req.getOrDefault("minCGPA", "7.0")));
                if (req.containsKey("allowedBranches")) {
                    d.setAllowedBranches(Arrays.asList(req.get("allowedBranches").split(",")));
                }
                d.setMaxActiveBacklogs(Integer.parseInt(req.getOrDefault("maxActiveBacklogs", "0")));
                d.setRequiredGraduationYear(Integer.parseInt(req.getOrDefault("requiredGraduationYear", "2025")));
                d.setStatus(PlacementDrive.DriveStatus.UPCOMING);
                driveManager.createDrive(d);
                notificationManager.addNotification("ALL", "New placement drive: " + d.getTitle() + " by " + d.getCompanyName());
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":\"" + id + "\"}");
            }
        }
    }

    private class EligibilityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> q = parseQuery(query);
            String driveId = q.get("driveId");
            String studentId = q.get("studentId");
            if (driveId == null || studentId == null) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Missing driveId or studentId\"}");
                return;
            }
            EligibilityResult res = eligibilityService.checkStudentEligibility(studentId, driveId);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("eligible", res.isEligible());
            map.put("studentId", res.getStudentId());
            map.put("driveId", res.getDriveId());
            map.put("reasons", res.getReasons());
            sendJsonResponse(exchange, 200, toJson(map));
        }
    }

    private class ApplicationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> q = parseQuery(query);
                List<Application> list;
                if (q.containsKey("studentId")) {
                    list = applicationManager.getByStudent(q.get("studentId"));
                } else if (q.containsKey("driveId")) {
                    list = applicationManager.getByDrive(q.get("driveId"));
                } else {
                    list = applicationManager.getAllApplications();
                }
                List<Map<String, String>> out = new ArrayList<>();
                for (Application a : list) out.add(a.toMap());
                sendJsonResponse(exchange, 200, toJson(out));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                try {
                    Application app = applicationService.submitApplication(req.get("studentId"), req.get("driveId"));
                    persistAll();
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":\"" + app.getId() + "\"}");
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String appId = req.get("id");
                ApplicationStatus status = ApplicationStatus.valueOf(req.getOrDefault("status", "APPLIED"));
                String remarks = req.getOrDefault("remarks", "");
                applicationManager.updateStatus(appId, status, remarks);
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true}");
            }
        }
    }

    private class InterviewsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> q = parseQuery(query);
                List<Interview> list;
                if (q.containsKey("studentId")) list = interviewManager.getByStudent(q.get("studentId"));
                else list = interviewManager.getAllInterviews();
                List<Map<String, String>> out = new ArrayList<>();
                for (Interview i : list) out.add(i.toMap());
                sendJsonResponse(exchange, 200, toJson(out));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String id = "INT" + String.format("%03d", interviewManager.getAllInterviews().size() + 1);
                InterviewRound round;
                try { round = InterviewRound.valueOf(req.getOrDefault("roundType", "TECHNICAL_ROUND_1")); }
                catch (Exception e) { round = InterviewRound.TECHNICAL_ROUND_1; }
                Interview i = new Interview(id, req.get("applicationId"), req.get("studentId"), req.get("driveId"),
                        req.get("companyName"), round, req.get("scheduledDate"), req.get("scheduledTime"), req.get("venue"));
                interviewManager.addInterview(i);
                interviewScheduler.scheduleInterview(i);
                notificationManager.addNotification(i.getStudentId(), "New interview scheduled with " + i.getCompanyName() + " on " + i.getScheduledDate());
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":\"" + id + "\"}");
            }
        }
    }

    private class ResultsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Map<String, String>> out = new ArrayList<>();
                for (PlacementResult r : resultManager.getAllResults()) out.add(r.toMap());
                sendJsonResponse(exchange, 200, toJson(out));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String id = "RES" + String.format("%03d", resultManager.getAllResults().size() + 1);
                PlacementResult r = new PlacementResult(id, req.get("studentId"), req.getOrDefault("driveId", "DIRECT"),
                        req.get("companyName"), Double.parseDouble(req.getOrDefault("packageLPA", "0")), req.get("position"));
                Student s = studentManager.getStudentById(req.get("studentId"));
                if (s != null) {
                    r.setStudentName(s.getName());
                    r.setRollNumber(s.getRollNumber());
                    s.setPlaced(true);
                    s.setPlacedCompany(r.getCompanyName());
                    s.setPackageOffered(r.getPackageLPA());
                    studentManager.updateStudent(s);
                }
                resultManager.addResult(r);
                notificationManager.addNotification(r.getStudentId(), "Congratulations! Selected by " + r.getCompanyName() + " (" + r.getPackageLPA() + " LPA)!");
                persistAll();
                sendJsonResponse(exchange, 200, "{\"success\":true,\"id\":\"" + id + "\"}");
            }
        }
    }

    private class StatisticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            placementStatistics.calculate(studentManager.getAllStudents(), resultManager.getAllResults());
            sendJsonResponse(exchange, 200, toJson(placementStatistics.toMap()));
        }
    }

    private class NotificationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> q = parseQuery(query);
                String userId = q.getOrDefault("userId", "ALL");
                List<NotificationManager.Notification> list = notificationManager.getForUser(userId);
                List<Map<String, String>> out = new ArrayList<>();
                for (NotificationManager.Notification n : list) out.add(n.toMap());
                sendJsonResponse(exchange, 200, toJson(out));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> req = parseSimpleJson(body);
                String message = req.getOrDefault("message", "");
                String userId = req.getOrDefault("userId", "ALL");
                notificationManager.addNotification(userId, message);
                sendJsonResponse(exchange, 200, "{\"success\":true}");
            }
        }
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleCors(exchange)) return;
            URI uri = exchange.getRequestURI();
            String pathStr = uri.getPath();
            if (pathStr == null || pathStr.equals("/")) pathStr = "/index.html";

            Path target = webRoot.resolve(pathStr.substring(1)).normalize();
            if (!target.startsWith(webRoot) || !Files.exists(target) || Files.isDirectory(target)) {
                target = webRoot.resolve("index.html");
                if (!Files.exists(target)) {
                    String err = "<h1>404 Not Found</h1><p>Web UI files not found at: " + webRoot + "</p>";
                    byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(404, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
                    return;
                }
            }

            String contentType = probeContentType(target.getFileName().toString());
            byte[] fileBytes = Files.readAllBytes(target);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(fileBytes); }
        }
    }

    // ----------------------------------------------------
    // UTILITIES
    // ----------------------------------------------------
    private boolean handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String probeContentType(String filename) {
        String name = filename.toLowerCase();
        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (name.endsWith(".json")) return "application/json; charset=UTF-8";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return result;
        String trimmed = json.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        String[] pairs = trimmed.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String key = cleanJsonToken(kv[0]);
                String val = cleanJsonToken(kv[1]);
                result.put(key, val);
            }
        }
        return result;
    }

    private String cleanJsonToken(String token) {
        String t = token.trim();
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1);
        }
        return t.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    @SuppressWarnings("unchecked")
    private String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Collection) {
            Collection<?> col = (Collection<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
