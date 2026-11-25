// PLACEMENT Frontend Application Engine
// Handles all actions from Terminal Interface in a clean, visual web format

const API = {
  get: async (url) => {
    try {
      const res = await fetch(url);
      return await res.json();
    } catch (e) {
      console.error(e);
      showToast('Network error: Is backend running on port 8080?', 'error');
      return null;
    }
  },
  post: async (url, data) => {
    try {
      const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      return await res.json();
    } catch (e) {
      console.error(e);
      showToast('Error submitting request to backend', 'error');
      return null;
    }
  },
  put: async (url, data) => {
    try {
      const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      return await res.json();
    } catch (e) {
      console.error(e);
      showToast('Error updating status', 'error');
      return null;
    }
  }
};

let user = JSON.parse(sessionStorage.getItem('placement_user') || 'null');

function showToast(msg, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const t = document.createElement('div');
  t.className = `toast toast-${type}`;
  t.innerText = msg;
  container.appendChild(t);
  setTimeout(() => t.remove(), 4000);
}

document.addEventListener('DOMContentLoaded', () => {
  if (!user && window.location.pathname.includes('dashboard.html')) {
    user = { role: 'ADMIN', name: 'Placement Officer', username: 'admin' };
  }

  if (document.getElementById('headerUserName')) {
    document.getElementById('headerUserName').innerText = user.name || user.username || 'User';
    document.getElementById('headerUserSub').innerText = user.role;
    document.getElementById('userRoleBadge').innerText = user.role;
  }

  if (document.getElementById('logoutBtn')) {
    document.getElementById('logoutBtn').addEventListener('click', () => {
      sessionStorage.removeItem('placement_user');
      window.location.href = 'index.html';
    });
  }

  setupClock();
  buildSidebar();
  loadInitialSection();
  lucide.createIcons();
});

function setupClock() {
  const clk = document.getElementById('systemClock');
  if (clk) {
    setInterval(() => {
      const now = new Date();
      clk.innerText = now.toLocaleDateString() + ' ' + now.toLocaleTimeString();
    }, 1000);
  }
}

function buildSidebar() {
  const nav = document.getElementById('sidebarNav');
  if (!nav) return;
  nav.innerHTML = '';

  const menus = {
    STUDENT: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview' },
      { id: 'profile', icon: 'user', label: '1. View/Update Profile' },
      { id: 'drives', icon: 'briefcase', label: '2. Placement Drives' },
      { id: 'eligibility', icon: 'check-square', label: '3. Check Eligibility' },
      { id: 'applications', icon: 'file-check', label: '4. Track Applications' },
      { id: 'interviews', icon: 'calendar', label: '5. Interview Schedule' },
      { id: 'results', icon: 'award', label: '6. Offers & Results' },
      { id: 'notifications', icon: 'bell', label: '7. Announcements' }
    ],
    ADMIN: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview & KPIs' },
      { id: 'students', icon: 'users', label: '1. Student Directory' },
      { id: 'companies', icon: 'building-2', label: '2. Manage Companies' },
      { id: 'drives', icon: 'calendar-plus', label: '3. Create / View Drives' },
      { id: 'applications', icon: 'inbox', label: '4. Manage Applications' },
      { id: 'interviews', icon: 'calendar-clock', label: '5. Schedule Interview' },
      { id: 'results', icon: 'trophy', label: '6. Update Results' },
      { id: 'analytics', icon: 'bar-chart-3', label: '7. Placement Stats' },
      { id: 'announcements', icon: 'megaphone', label: '8. Post Announcement' }
    ],
    RECRUITER: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview' },
      { id: 'drives', icon: 'briefcase', label: '1. Active Drives' },
      { id: 'applicants', icon: 'user-check', label: '2. View Applicants' },
      { id: 'candidates', icon: 'search', label: '3. Filter Candidates' },
      { id: 'interviews', icon: 'calendar', label: '4. Interview Sessions' }
    ]
  };

  const role = user ? user.role : 'STUDENT';
  const roleMenus = menus[role] || menus.STUDENT;

  roleMenus.forEach((m, idx) => {
    const a = document.createElement('a');
    a.href = '#';
    a.className = `nav-item ${idx === 0 ? 'active' : ''}`;
    a.innerHTML = `<i data-lucide="${m.icon}"></i> <span>${m.label}</span>`;
    a.addEventListener('click', (e) => {
      e.preventDefault();
      document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
      a.classList.add('active');
      renderSection(m.id);
    });
    nav.appendChild(a);
  });
  lucide.createIcons();
}

function loadInitialSection() {
  renderSection('overview');
}

async function renderSection(section) {
  const content = document.getElementById('contentArea');
  const title = document.getElementById('pageTitle');
  if (!content) return;

  // ---------------------------------------------
  // OVERVIEW
  // ---------------------------------------------
  if (section === 'overview') {
    title.innerText = 'System Dashboard & Operations';
    const stats = await API.get('/api/statistics') || { totalStudents: 3, placedStudents: 1, placementPercentage: '33.3', averagePackage: '8.50' };
    content.innerHTML = `
      <div class="kpi-grid">
        <div class="kpi-card">
          <i data-lucide="users" class="kpi-icon icon-blue"></i>
          <div><h3>${stats.totalStudents || 0}</h3><p>Total Registered Students</p></div>
        </div>
        <div class="kpi-card">
          <i data-lucide="check-circle" class="kpi-icon icon-green"></i>
          <div><h3>${stats.placedStudents || 0}</h3><p>Students Placed</p></div>
        </div>
        <div class="kpi-card">
          <i data-lucide="percent" class="kpi-icon icon-indigo"></i>
          <div><h3>${stats.placementPercentage || 0}%</h3><p>Placement Percentage</p></div>
        </div>
        <div class="kpi-card">
          <i data-lucide="trending-up" class="kpi-icon icon-amber"></i>
          <div><h3>${stats.averagePackage || '0.00'} LPA</h3><p>Average Package</p></div>
        </div>
      </div>

      <div class="card" style="margin-top: 1.5rem;">
        <div class="card-header-flex">
          <h3><i data-lucide="info" style="width:18px;height:18px;vertical-align:middle"></i> Available Roles & Actions in System</h3>
        </div>
        <p style="color:var(--text-secondary);font-size:0.9rem;margin-bottom:1rem">
          Every action implemented in the <b>Terminal Console Menu</b> is completely executable here through visual forms and real-time backend updates.
        </p>
        <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:1rem;">
          <div style="background:#f8fafc;padding:1rem;border-radius:8px;border:1px solid #e2e8f0;">
            <strong style="color:#1e40af">Student Capabilities:</strong>
            <ul style="margin-left:1.25rem;margin-top:0.5rem;font-size:0.85rem;color:#475569;line-height:1.5">
              <li>Check Eligibility with explicit reasons</li>
              <li>1-Click Application (duplicate protected)</li>
              <li>Live application status tracking</li>
              <li>View interview schedules and venue</li>
            </ul>
          </div>
          <div style="background:#f8fafc;padding:1rem;border-radius:8px;border:1px solid #e2e8f0;">
            <strong style="color:#1e40af">TPO / Admin Capabilities:</strong>
            <ul style="margin-left:1.25rem;margin-top:0.5rem;font-size:0.85rem;color:#475569;line-height:1.5">
              <li>Add company partner records</li>
              <li>Launch placement drives with criteria</li>
              <li>Shortlist / Reject candidate applications</li>
              <li>Schedule interview rounds (with conflict checks)</li>
              <li>Record final selection offers and packages</li>
            </ul>
          </div>
        </div>
      </div>
    `;
    lucide.createIcons();
  }

  // ---------------------------------------------
  // STUDENT: PROFILE
  // ---------------------------------------------
  else if (section === 'profile') {
    title.innerText = 'Student Profile & Academic Record';
    const studentId = user && user.id ? user.id : 'STU001';
    const s = await API.get(`/api/students?id=${studentId}`) || {};
    content.innerHTML = `
      <div class="card" style="max-width:700px;">
        <h3>Academic & Contact Details</h3>
        <form onsubmit="saveStudentProfile(event)" style="margin-top:1.25rem;">
          <input type="hidden" id="profId" value="${s.id || studentId}">
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem;">
            <div class="form-group">
              <label>Full Name</label>
              <input type="text" id="profName" class="form-control" value="${s.name || ''}" required>
            </div>
            <div class="form-group">
              <label>Roll Number</label>
              <input type="text" class="form-control" value="${s.rollNumber || ''}" disabled>
            </div>
            <div class="form-group">
              <label>Branch</label>
              <input type="text" class="form-control" value="${s.branch || ''}" disabled>
            </div>
            <div class="form-group">
              <label>CGPA</label>
              <input type="number" step="0.01" min="0" max="10" id="profCgpa" class="form-control" value="${s.cgpa || ''}" required>
            </div>
            <div class="form-group">
              <label>Active Backlogs</label>
              <input type="number" min="0" id="profBacklogs" class="form-control" value="${s.activeBacklogs || '0'}" required>
            </div>
            <div class="form-group">
              <label>Email</label>
              <input type="email" id="profEmail" class="form-control" value="${s.email || ''}" required>
            </div>
          </div>
          <button type="submit" class="btn btn-primary" style="margin-top:1rem;">Save Profile Changes</button>
        </form>
      </div>
    `;
  }

  // ---------------------------------------------
  // DRIVES (Student & Admin)
  // ---------------------------------------------
  else if (section === 'drives') {
    title.innerText = 'Placement Drives';
    const drives = await API.get('/api/drives') || [];
    const companies = await API.get('/api/companies') || [];
    content.innerHTML = `
      ${user && user.role === 'ADMIN' ? `
        <div class="card" style="margin-bottom:1.5rem;">
          <h3><i data-lucide="plus-circle" style="width:18px;height:18px;vertical-align:middle"></i> Create Placement Drive (TPO Action)</h3>
          <form onsubmit="createDriveSubmit(event)" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:0.75rem;margin-top:1rem;">
            <div>
              <label style="font-size:0.8rem;font-weight:600">Company</label>
              <select id="drvCompany" class="form-control" required>
                ${companies.map(c => `<option value="${c.id}|${c.name}">${c.name}</option>`).join('')}
              </select>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Drive Title</label>
              <input type="text" id="drvTitle" class="form-control" placeholder="e.g. Associate Engineer 2025" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Drive Date</label>
              <input type="date" id="drvDate" class="form-control" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Min CGPA</label>
              <input type="number" step="0.1" min="0" max="10" id="drvCgpa" class="form-control" value="7.0" required>
            </div>
            <div style="align-self:end">
              <button type="submit" class="btn btn-primary" style="width:100%">Publish Drive</button>
            </div>
          </form>
        </div>
      ` : ''}

      <div class="card">
        <h3>Available Placement Drives (${drives.length})</h3>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>ID</th><th>Drive Title</th><th>Company</th><th>Drive Date</th><th>Min CGPA</th><th>Actions</th></tr>
          </thead>
          <tbody>
            ${drives.map(d => `
              <tr>
                <td><strong>${d.id}</strong></td>
                <td>${d.title}</td>
                <td>${d.companyName}</td>
                <td>${d.driveDate}</td>
                <td>${d.minCGPA}</td>
                <td>
                  <button class="btn btn-secondary btn-sm" onclick="checkEligibility('${d.id}')">Check Eligibility</button>
                  ${user && user.role === 'STUDENT' ? `
                    <button class="btn btn-primary btn-sm" onclick="applyDrive('${d.id}')">1-Click Apply</button>
                  ` : ''}
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
    lucide.createIcons();
  }

  // ---------------------------------------------
  // ELIGIBILITY CHECKER
  // ---------------------------------------------
  else if (section === 'eligibility') {
    title.innerText = 'Eligibility Engine Simulator';
    const drives = await API.get('/api/drives') || [];
    const studentId = user && user.id ? user.id : 'STU001';
    content.innerHTML = `
      <div class="card" style="max-width:700px;">
        <h3>Automatic Multi-Criteria Eligibility Verification</h3>
        <p style="font-size:0.85rem;color:var(--text-secondary);margin:0.5rem 0 1.25rem 0;">
          Select any active campus placement drive to evaluate your CGPA, branch, active backlogs, and graduation year against recruiter criteria.
        </p>
        <div class="form-group">
          <label>Select Drive to Test</label>
          <select id="eligDriveSelect" class="form-control">
            ${drives.map(d => `<option value="${d.id}">${d.id} - ${d.title} (${d.companyName})</option>`).join('')}
          </select>
        </div>
        <button class="btn btn-primary" onclick="runEligibilityCheck()">Run Eligibility Checker</button>

        <div id="eligResultBox" style="margin-top:1.5rem;display:none;padding:1.25rem;border-radius:8px;"></div>
      </div>
    `;
  }

  // ---------------------------------------------
  // APPLICATIONS
  // ---------------------------------------------
  else if (section === 'applications' || section === 'applicants') {
    title.innerText = 'Candidate Application Pipeline';
    const url = user && user.role === 'STUDENT' ? `/api/applications?studentId=${user.id || 'STU001'}` : '/api/applications';
    const apps = await API.get(url) || [];
    content.innerHTML = `
      <div class="card">
        <div class="card-header-flex">
          <h3>Submitted Applications (${apps.length})</h3>
        </div>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>App ID</th><th>Student</th><th>Company</th><th>Status</th><th>Applied Date</th>${user && user.role !== 'STUDENT' ? '<th>TPO Action</th>' : ''}</tr>
          </thead>
          <tbody>
            ${apps.map(a => `
              <tr>
                <td><strong>${a.id}</strong></td>
                <td>${a.studentName || a.studentId}</td>
                <td>${a.companyName}</td>
                <td><span class="badge ${a.status === 'SELECTED' ? 'badge-success' : a.status === 'SHORTLISTED' ? 'badge-indigo' : a.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}">${a.status}</span></td>
                <td>${a.appliedAt}</td>
                ${user && user.role !== 'STUDENT' ? `
                  <td>
                    <button class="btn btn-sm btn-primary" onclick="updateAppStatus('${a.id}','SHORTLISTED')">Shortlist</button>
                    <button class="btn btn-sm btn-danger" onclick="updateAppStatus('${a.id}','REJECTED')">Reject</button>
                  </td>
                ` : ''}
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  }

  // ---------------------------------------------
  // INTERVIEWS
  // ---------------------------------------------
  else if (section === 'interviews') {
    title.innerText = 'Interview Schedules & Conflict-Free Booking';
    const url = user && user.role === 'STUDENT' ? `/api/interviews?studentId=${user.id || 'STU001'}` : '/api/interviews';
    const interviews = await API.get(url) || [];
    const apps = await API.get('/api/applications') || [];

    content.innerHTML = `
      ${user && user.role !== 'STUDENT' ? `
        <div class="card" style="margin-bottom:1.5rem;">
          <h3><i data-lucide="calendar-plus" style="width:18px;height:18px;vertical-align:middle"></i> Schedule Interview (with Conflict Check)</h3>
          <form onsubmit="scheduleInterviewSubmit(event)" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:0.75rem;margin-top:1rem;">
            <div>
              <label style="font-size:0.8rem;font-weight:600">Candidate Application</label>
              <select id="intApp" class="form-control" required>
                ${apps.map(a => `<option value="${a.id}|${a.studentId}|${a.driveId}|${a.companyName}">${a.studentName || a.studentId} - ${a.companyName}</option>`).join('')}
              </select>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Round Type</label>
              <select id="intRound" class="form-control">
                <option value="TECHNICAL_ROUND_1">Technical Round 1</option>
                <option value="CODING_TEST">Coding Test</option>
                <option value="HR_ROUND">HR Round</option>
                <option value="APTITUDE_TEST">Aptitude Test</option>
              </select>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Date</label>
              <input type="date" id="intDate" class="form-control" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Time</label>
              <input type="text" id="intTime" class="form-control" placeholder="10:00 AM" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Venue / Link</label>
              <input type="text" id="intVenue" class="form-control" placeholder="Room 204 or Meet Link" required>
            </div>
            <div style="align-self:end">
              <button type="submit" class="btn btn-primary" style="width:100%">Book Interview</button>
            </div>
          </form>
        </div>
      ` : ''}

      <div class="card">
        <h3>Scheduled Interview Sessions (${interviews.length})</h3>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>ID</th><th>Company</th><th>Round</th><th>Date &amp; Time</th><th>Venue</th><th>Status</th></tr>
          </thead>
          <tbody>
            ${interviews.map(i => `
              <tr>
                <td><strong>${i.id}</strong></td>
                <td>${i.companyName}</td>
                <td>${i.roundType}</td>
                <td>${i.scheduledDate} ${i.scheduledTime}</td>
                <td>${i.venue}</td>
                <td><span class="badge badge-indigo">${i.status}</span></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
    lucide.createIcons();
  }

  // ---------------------------------------------
  // RESULTS & SELECTIONS
  // ---------------------------------------------
  else if (section === 'results') {
    title.innerText = 'Placement Offers & Selections';
    const results = await API.get('/api/results') || [];
    const students = await API.get('/api/students') || [];
    content.innerHTML = `
      ${user && user.role !== 'STUDENT' ? `
        <div class="card" style="margin-bottom:1.5rem;">
          <h3><i data-lucide="award" style="width:18px;height:18px;vertical-align:middle"></i> Record Selection Offer (TPO Action)</h3>
          <form onsubmit="recordOfferSubmit(event)" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:0.75rem;margin-top:1rem;">
            <div>
              <label style="font-size:0.8rem;font-weight:600">Select Student</label>
              <select id="offStudent" class="form-control" required>
                ${students.map(s => `<option value="${s.id}">${s.name} (${s.rollNumber})</option>`).join('')}
              </select>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Company Name</label>
              <input type="text" id="offCompany" class="form-control" placeholder="e.g. TechCorp Solutions" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Position / Role</label>
              <input type="text" id="offRole" class="form-control" placeholder="Software Engineer" required>
            </div>
            <div>
              <label style="font-size:0.8rem;font-weight:600">Package (LPA)</label>
              <input type="number" step="0.1" min="1" id="offPkg" class="form-control" placeholder="10.5" required>
            </div>
            <div style="align-self:end">
              <button type="submit" class="btn btn-primary" style="width:100%">Award Offer</button>
            </div>
          </form>
        </div>
      ` : ''}

      <div class="card">
        <h3>Final Placement Selection List</h3>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>Candidate</th><th>Company</th><th>Role</th><th>Package</th><th>Status</th></tr>
          </thead>
          <tbody>
            ${results.map(r => `
              <tr>
                <td><strong>${r.studentName || r.studentId}</strong> (${r.rollNumber || ''})</td>
                <td>${r.companyName}</td>
                <td>${r.position}</td>
                <td><strong style="color:#059669">${r.packageLPA} LPA</strong></td>
                <td><span class="badge badge-success">${r.status}</span></td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
    lucide.createIcons();
  }

  // ---------------------------------------------
  // COMPANIES (Admin)
  // ---------------------------------------------
  else if (section === 'companies') {
    title.innerText = 'Corporate Partner Directory';
    const companies = await API.get('/api/companies') || [];
    content.innerHTML = `
      <div class="card" style="margin-bottom:1.5rem;">
        <h3><i data-lucide="building" style="width:18px;height:18px;vertical-align:middle"></i> Add Company Partner</h3>
        <form onsubmit="addCompanySubmit(event)" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:0.75rem;margin-top:1rem;">
          <div>
            <label style="font-size:0.8rem;font-weight:600">Company Name</label>
            <input type="text" id="compName" class="form-control" placeholder="e.g. Google India" required>
          </div>
          <div>
            <label style="font-size:0.8rem;font-weight:600">Industry</label>
            <input type="text" id="compInd" class="form-control" placeholder="e.g. Cloud & AI" required>
          </div>
          <div>
            <label style="font-size:0.8rem;font-weight:600">Location</label>
            <input type="text" id="compLoc" class="form-control" placeholder="Bangalore" required>
          </div>
          <div>
            <label style="font-size:0.8rem;font-weight:600">Min CGPA</label>
            <input type="number" step="0.1" id="compCgpa" class="form-control" value="7.5" required>
          </div>
          <div style="align-self:end">
            <button type="submit" class="btn btn-primary" style="width:100%">Add Partner</button>
          </div>
        </form>
      </div>

      <div class="card">
        <h3>Registered Companies (${companies.length})</h3>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>ID</th><th>Name</th><th>Industry</th><th>Location</th><th>Min CGPA</th></tr>
          </thead>
          <tbody>
            ${companies.map(c => `
              <tr>
                <td><strong>${c.id}</strong></td>
                <td>${c.name}</td>
                <td>${c.industry}</td>
                <td>${c.location}</td>
                <td>${c.minCGPA}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
    lucide.createIcons();
  }

  // ---------------------------------------------
  // STUDENTS DIRECTORY (Admin)
  // ---------------------------------------------
  else if (section === 'students') {
    title.innerText = 'Student Directory & Records';
    const students = await API.get('/api/students') || [];
    content.innerHTML = `
      <div class="card">
        <h3>Enrolled Candidates (${students.length})</h3>
        <table class="data-table" style="margin-top:1rem;">
          <thead>
            <tr><th>Roll No</th><th>Name</th><th>Branch</th><th>CGPA</th><th>Placed?</th><th>Company</th></tr>
          </thead>
          <tbody>
            ${students.map(s => `
              <tr>
                <td><strong>${s.rollNumber}</strong></td>
                <td>${s.name}</td>
                <td><span class="tag">${s.branch}</span></td>
                <td>${s.cgpa}</td>
                <td><span class="badge ${s.isPlaced === 'true' ? 'badge-success' : 'badge-warning'}">${s.isPlaced === 'true' ? 'PLACED' : 'UNPLACED'}</span></td>
                <td>${s.placedCompany || '—'}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    `;
  }

  // ---------------------------------------------
  // ANNOUNCEMENTS
  // ---------------------------------------------
  else if (section === 'announcements' || section === 'notifications') {
    title.innerText = 'Announcements & Alerts';
    const notifs = await API.get('/api/notifications') || [];
    content.innerHTML = `
      ${user && user.role === 'ADMIN' ? `
        <div class="card" style="margin-bottom:1.5rem;">
          <h3><i data-lucide="megaphone" style="width:18px;height:18px;vertical-align:middle"></i> Broadcast Announcement</h3>
          <form onsubmit="broadcastSubmit(event)" style="display:flex;gap:0.75rem;margin-top:1rem;">
            <input type="text" id="annMsg" class="form-control" placeholder="Enter message for all students..." required>
            <button type="submit" class="btn btn-primary" style="white-space:nowrap">Broadcast Message</button>
          </form>
        </div>
      ` : ''}

      <div class="card">
        <h3>Campus Notice Board</h3>
        <div style="margin-top:1rem;display:flex;flex-direction:column;gap:0.75rem;">
          ${notifs.map(n => `
            <div style="background:#f8fafc;padding:0.85rem 1rem;border-radius:8px;border-left:4px solid #3b82f6;">
              <div style="font-size:0.75rem;color:#64748b;margin-bottom:0.25rem">${n.timestamp} &bull; To: ${n.userId}</div>
              <div style="font-size:0.95rem;font-weight:500">${n.message}</div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
    lucide.createIcons();
  }
}

// ----------------------------------------------------
// ACTION HANDLERS
// ----------------------------------------------------

async function runEligibilityCheck() {
  const driveId = document.getElementById('eligDriveSelect').value;
  const studentId = user && user.id ? user.id : 'STU001';
  const res = await API.get(`/api/eligibility?driveId=${driveId}&studentId=${studentId}`);
  const box = document.getElementById('eligResultBox');
  if (!box || !res) return;

  box.style.display = 'block';
  if (res.eligible) {
    box.style.background = '#ecfdf5';
    box.style.border = '1px solid #a7f3d0';
    box.style.color = '#065f46';
    box.innerHTML = `
      <h4 style="display:flex;align-items:center;gap:0.5rem;font-size:1.1rem;margin-bottom:0.4rem">
        ✓ CONGRATULATIONS! YOU ARE FULLY ELIGIBLE
      </h4>
      <p style="font-size:0.9rem">You satisfy all criteria (CGPA, Branch, Active Backlogs &amp; Grad Year) for Drive <b>${driveId}</b>.</p>
      <button class="btn btn-primary btn-sm" style="margin-top:0.75rem" onclick="applyDrive('${driveId}')">Apply to this Drive Now</button>
    `;
  } else {
    box.style.background = '#fef2f2';
    box.style.border = '1px solid #fecaca';
    box.style.color = '#991b1b';
    box.innerHTML = `
      <h4 style="display:flex;align-items:center;gap:0.5rem;font-size:1.1rem;margin-bottom:0.4rem">
        ✗ CRITERIA NOT MET
      </h4>
      <p style="font-size:0.9rem">You are currently ineligible for Drive <b>${driveId}</b> due to:</p>
      <ul style="margin-left:1.5rem;margin-top:0.4rem;font-size:0.85rem">
        ${(res.reasons || []).map(r => `<li>${r}</li>`).join('')}
      </ul>
    `;
  }
}

async function checkEligibility(driveId) {
  const studentId = (user && user.id) ? user.id : 'STU001';
  const res = await API.get(`/api/eligibility?driveId=${driveId}&studentId=${studentId}`);
  if (res) {
    if (res.eligible) {
      alert(`[ELIGIBILITY VERIFIED]\nCandidate is eligible to apply for drive ${driveId}!`);
    } else {
      alert(`[NOT ELIGIBLE]\nReasons:\n` + (res.reasons ? res.reasons.join('\n') : 'Requirements not met'));
    }
  }
}

async function applyDrive(driveId) {
  const studentId = (user && user.id) ? user.id : 'STU001';
  const res = await API.post('/api/applications', { studentId, driveId });
  if (res && res.success) {
    showToast('Application successfully submitted! ID: ' + res.id, 'success');
    renderSection('applications');
  } else {
    showToast(res ? res.error : 'Submission failed', 'error');
  }
}

async function saveStudentProfile(e) {
  e.preventDefault();
  const id = document.getElementById('profId').value;
  const name = document.getElementById('profName').value;
  const cgpa = document.getElementById('profCgpa').value;
  const activeBacklogs = document.getElementById('profBacklogs').value;
  const email = document.getElementById('profEmail').value;

  const res = await API.put('/api/students', { id, name, cgpa, activeBacklogs, email });
  if (res && res.success) {
    showToast('Profile updated successfully!', 'success');
  }
}

async function createDriveSubmit(e) {
  e.preventDefault();
  const compVal = document.getElementById('drvCompany').value.split('|');
  const title = document.getElementById('drvTitle').value;
  const driveDate = document.getElementById('drvDate').value;
  const minCGPA = document.getElementById('drvCgpa').value;

  const res = await API.post('/api/drives', {
    companyId: compVal[0],
    companyName: compVal[1],
    title, driveDate, minCGPA,
    maxActiveBacklogs: 0, requiredGraduationYear: 2025
  });
  if (res && res.success) {
    showToast('Placement drive published! ID: ' + res.id, 'success');
    renderSection('drives');
  }
}

async function updateAppStatus(id, status) {
  const res = await API.put('/api/applications', { id, status, remarks: 'Updated via Web Dashboard' });
  if (res && res.success) {
    showToast(`Application ${id} updated to ${status}`, 'success');
    renderSection('applications');
  }
}

async function scheduleInterviewSubmit(e) {
  e.preventDefault();
  const appParts = document.getElementById('intApp').value.split('|');
  const roundType = document.getElementById('intRound').value;
  const scheduledDate = document.getElementById('intDate').value;
  const scheduledTime = document.getElementById('intTime').value;
  const venue = document.getElementById('intVenue').value;

  const res = await API.post('/api/interviews', {
    applicationId: appParts[0],
    studentId: appParts[1],
    driveId: appParts[2],
    companyName: appParts[3],
    roundType, scheduledDate, scheduledTime, venue
  });
  if (res && res.success) {
    showToast('Interview session scheduled! ID: ' + res.id, 'success');
    renderSection('interviews');
  }
}

async function recordOfferSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById('offStudent').value;
  const companyName = document.getElementById('offCompany').value;
  const position = document.getElementById('offRole').value;
  const packageLPA = document.getElementById('offPkg').value;

  const res = await API.post('/api/results', { studentId, companyName, position, packageLPA });
  if (res && res.success) {
    showToast('Offer recorded and student marked as PLACED!', 'success');
    renderSection('results');
  }
}

async function addCompanySubmit(e) {
  e.preventDefault();
  const name = document.getElementById('compName').value;
  const industry = document.getElementById('compInd').value;
  const location = document.getElementById('compLoc').value;
  const minCGPA = document.getElementById('compCgpa').value;

  const res = await API.post('/api/companies', { name, industry, location, minCGPA });
  if (res && res.success) {
    showToast('Company partner registered! ID: ' + res.id, 'success');
    renderSection('companies');
  }
}

async function broadcastSubmit(e) {
  e.preventDefault();
  const message = document.getElementById('annMsg').value;
  await API.post('/api/notifications', { message, userId: 'ALL' });
  showToast('Announcement posted to noticeboard!', 'success');
  renderSection('announcements');
}
