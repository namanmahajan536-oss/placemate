# PLACEMENT – Smart Campus Placement Management System

A production-grade, enterprise-ready College Placement Management System built with **Pure Java (OOP & DSA)**, an embedded lightweight HTTP REST API server, a modern web dashboard, and an independent, interactive terminal interface.

---

## 1. Project Overview & Architecture

PLACEMENT connects students, placement officers (TPOs), and corporate recruiters onto a single unified platform. The Java backend serves as the single source of truth across all interfaces.

```
PLACEMENT/
├── src/main/java/placement/
│   ├── Main.java                        # System Launcher (CLI / Web)
│   ├── authentication/                  # User, Auth, Password & Registration
│   ├── student/                         # Student Profile, Skills, Resumes
│   ├── company/                         # Companies, Drives, Job Postings, Recruiters
│   ├── eligibility/                     # Automated Eligibility Rules Engine
│   ├── application/                     # Application Tracking & Shortlisting
│   ├── interview/                       # Conflict-free Interview Scheduler
│   ├── results/                         # Results, Package Analyzer & Analytics
│   ├── server/                          # Embedded Java HTTP REST API & Static Server
│   ├── ui/                              # Interactive Terminal Console & Menus
│   ├── utilities/                       # Data persistence, Searching & Sorting
│   └── exception/                       # Custom domain exception hierarchy
├── web/                                 # Modern Responsive SaaS Frontend
│   ├── index.html                       # Landing page, Login & Registration
│   ├── dashboard.html                   # Multi-role single-page dashboard
│   ├── styles.css                       # Modern Navy/Indigo SaaS theme
│   └── app.js                           # Frontend API engine & Charts
├── data/                                # Persistent file-based database (.dat)
├── out/                                 # Compiled Java bytecode
├── run.bat                              # Windows execution script
├── run.sh                               # Linux / macOS execution script
└── README.md                            # Comprehensive documentation
```

---

## 2. Technology Stack

- **Backend**: Pure Java (JDK 8 or newer). Zero external Maven/Gradle bloat.
- **Embedded Server**: `com.sun.net.httpserver.HttpServer` with RESTful JSON endpoints.
- **Data Structures**:
  - `ArrayList` and `LinkedList` for dynamic entity collections.
  - `HashMap` for constant-time $O(1)$ lookups and session state.
  - `HashSet` for preventing duplicate applications across drives.
  - `Queue` (FIFO) for ordering placement drive batches and notifications.
  - `TreeMap` for chronological interview scheduling and conflict avoidance.
- **Algorithms**:
  - **Sorting**: Custom Comparators, `Collections.sort`, and DSA bubble sort implementation.
  - **Searching**: Linear search, Binary search on sorted lists, and fuzzy matching.
- **Storage**: Multi-entity file-based persistent store (`data/*.dat`) that maintains data across restarts.
- **Frontend**: Semantic HTML5, Vanilla JavaScript (ES6+), Modern CSS3 with CSS variables, Lucide icons, and Chart.js.

---

## 3. Four-Member Team Division

### Member 1: Authentication & Student Management
- **Java Files**: `User.java`, `Admin.java`, `AuthenticationService.java`, `RegistrationService.java`, `PasswordManager.java`, `Student.java`, `StudentProfile.java`, `StudentManager.java`, `StudentService.java`, `Skill.java`, `Resume.java`, `ResumeManager.java`, `StudentValidator.java`.
- **Responsibilities**: User inheritance hierarchy, password hashing, student profiles, academic metrics, and resume handling.

### Member 2: Companies & Placement Drives
- **Java Files**: `Company.java`, `CompanyManager.java`, `CompanyService.java`, `Recruiter.java`, `RecruiterManager.java`, `JobDescription.java`, `PlacementDrive.java`, `PlacementDriveManager.java`.
- **Responsibilities**: Corporate partner profiles, placement drive creation, queue-based drive processing, and job postings.

### Member 3: Eligibility & Application Management
- **Java Files**: `EligibilityCriteria.java`, `EligibilityResult.java`, `CGPAChecker.java`, `BacklogChecker.java`, `BranchValidator.java`, `EligibilityChecker.java`, `EligibilityService.java`, `Application.java`, `ApplicationManager.java`, `ApplicationService.java`, `ApplicationStatus.java`, `ApplicationTracker.java`, `ShortlistingManager.java`, `ApplicationValidator.java`.
- **Responsibilities**: Automated multi-rule eligibility engine, duplicate application prevention via `HashSet`, shortlisting, and candidate status pipelines.

### Member 4: Interviews, Results & System Integration
- **Java Files**: `Interview.java`, `InterviewRound.java`, `InterviewScheduler.java`, `InterviewManager.java`, `InterviewValidator.java`, `PlacementResult.java`, `ResultManager.java`, `PlacementStatistics.java`, `PackageAnalyzer.java`, `DataManager.java`, `FileManager.java`, `SearchManager.java`, `SortManager.java`, `ReportManager.java`, `NotificationManager.java`, `InputValidator.java`, `HttpApiServer.java`, `PlacementConsole.java`, `ConsoleMenu.java`, `ConsoleInput.java`, `Main.java`.
- **Responsibilities**: Conflict-free interview scheduling, placement analytics (average, median, highest packages), terminal UI, REST API server, frontend integration, and persistence.

---

## 4. How to Compile and Run

### Prerequisites
- Java Development Kit (JDK 8 or higher) installed and configured on your `PATH`.

### Option A: Using Windows PowerShell / Terminal

1. **Compile all Java files**:
   ```powershell
   Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName | Out-File -FilePath "sources.txt" -Encoding ascii
   javac -d out @sources.txt
   ```

2. **Launch Terminal Interface (Console Menu)**:
   ```powershell
   java -cp out placement.Main
   ```

3. **Launch Modern Web Server & Dashboard**:
   ```powershell
   java -cp out placement.Main --web
   ```
   Then open your browser at: **`http://localhost:8080/`**

### Option B: Using Helper Scripts

- **On Windows**: Double-click or run `run.bat`
- **On Linux / macOS**: Run `chmod +x run.sh && ./run.sh`

---

## 5. Demo Credentials

The system automatically initializes demo data upon initial startup:

| Role | Username | Password | Notes / Identifiers |
|---|---|---|---|
| **TPO / Admin** | `admin` | `admin123` | Full access to student & drive management |
| **Student 1** | `student1` | `pass123` | Rahul Sharma (CSE, CGPA 8.5, Placed) |
| **Student 2** | `student2` | `pass123` | Priya Patel (ECE, CGPA 7.8, Eligible) |
| **Student 3** | `student3` | `pass123` | Amit Kumar (ME, CGPA 6.8, Unplaced) |
| **Recruiter** | `recruiter1` | `pass123` | Vikram Malhotra (TechCorp Solutions) |

---

## 6. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/status` | System health check and global counts |
| `POST` | `/api/auth/login` | User login (returns role, token & details) |
| `POST` | `/api/auth/register` | Student registration |
| `GET` | `/api/students` | Retrieve all students or filter by ID |
| `PUT` | `/api/students` | Update student profile and metrics |
| `GET` | `/api/companies` | List corporate partners |
| `POST` | `/api/companies` | Register a new company partner |
| `GET` | `/api/drives` | List all active and upcoming drives |
| `POST` | `/api/drives` | Create a new campus placement drive |
| `GET` | `/api/eligibility?driveId=X&studentId=Y` | Check student eligibility with detailed reasons |
| `GET` | `/api/applications` | Query applications by student or drive |
| `POST` | `/api/applications` | Submit application (validates eligibility) |
| `PUT` | `/api/applications` | Update application status (Shortlist/Reject) |
| `GET` | `/api/interviews` | View scheduled interviews |
| `POST` | `/api/interviews` | Schedule new interview round |
| `GET` | `/api/results` | View placement offers and results |
| `POST` | `/api/results` | Record selection offer and salary package |
| `GET` | `/api/statistics` | Real-time placement analytics and metrics |
| `GET` | `/api/notifications` | View system alerts and broadcast updates |
