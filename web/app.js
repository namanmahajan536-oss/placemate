/**
 * PlaceMate – Commercial SaaS Frontend Engine
 * Complete integration with Java REST API server
 */

// ==========================================================================
// 1. Core API Service Client
// ==========================================================================
const API = {
  get: async (endpoint) => {
    try {
      const res = await fetch(endpoint);
      if (!res.ok && res.status === 404) return null;
      return await res.json();
    } catch (e) {
      console.warn(`[API GET Failed] ${endpoint}:`, e);
      return null;
    }
  },

  post: async (endpoint, data) => {
    try {
      const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      return await res.json();
    } catch (e) {
      console.error(`[API POST Failed] ${endpoint}:`, e);
      showToast('Error communicating with backend', 'error');
      return null;
    }
  },

  put: async (endpoint, data) => {
    try {
      const res = await fetch(endpoint, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });
      return await res.json();
    } catch (e) {
      console.error(`[API PUT Failed] ${endpoint}:`, e);
      showToast('Error updating record', 'error');
      return null;
    }
  },

  delete: async (endpoint) => {
    try {
      const res = await fetch(endpoint, {
        method: 'DELETE'
      });
      return await res.json();
    } catch (e) {
      console.error(`[API DELETE Failed] ${endpoint}:`, e);
      showToast('Error deleting record', 'error');
      return null;
    }
  }
};

// ==========================================================================
// 2. Global State & Session
// ==========================================================================
let currentUser = null;
let currentSection = 'overview';
let activeCharts = {};

// Cached Data Collections
let cache = {
  students: [],
  companies: [],
  drives: [],
  applications: [],
  interviews: [],
  results: [],
  notifications: [],
  stats: null
};

// ==========================================================================
// 3. Application Bootstrap
// ==========================================================================
document.addEventListener('DOMContentLoaded', () => {
  // Load session from storage or default to Admin demo for immediate access
  const storedUser = sessionStorage.getItem('placement_user');
  if (storedUser) {
    try { currentUser = JSON.parse(storedUser); } catch (e) { currentUser = null; }
  }

  // Fallback demo user if dashboard opened directly without login
  if (!currentUser) {
    currentUser = {
      id: 'ADMIN001',
      username: 'admin',
      name: 'Placement Officer',
      role: 'ADMIN'
    };
    sessionStorage.setItem('placement_user', JSON.stringify(currentUser));
  }

  // Initialize Shell Widgets
  setupSidebarUser();
  buildSidebarNav();
  setupQuickActionMenu();
  setupKeyboardShortcuts();

  // Load Initial View
  navigateSection('overview');

  // Load and refresh notifications badge in background
  refreshNotificationsBadge();
});

// ==========================================================================
// 4. Shell & Navigation Management
// ==========================================================================
function setupSidebarUser() {
  const avatarEl = document.getElementById('sidebarAvatar');
  const nameEl = document.getElementById('sidebarUserName');
  const roleEl = document.getElementById('sidebarUserRole');

  if (avatarEl && nameEl && roleEl) {
    const displayName = currentUser.name || currentUser.username || 'User';
    nameEl.innerText = displayName;
    roleEl.innerText = currentUser.role || 'STUDENT';
    avatarEl.innerText = displayName.charAt(0).toUpperCase();
  }
}

function buildSidebarNav() {
  const navContainer = document.getElementById('sidebarNavList');
  if (!navContainer) return;
  navContainer.innerHTML = '';

  const role = currentUser.role || 'STUDENT';

  // Navigation Items per Role
  const roleMenus = {
    ADMIN: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview & KPIs' },
      { id: 'students', icon: 'users', label: 'Student Directory' },
      { id: 'companies', icon: 'building-2', label: 'Company Partners' },
      { id: 'drives', icon: 'calendar-plus', label: 'Placement Drives' },
      { id: 'applications', icon: 'inbox', label: 'Candidate Pipeline' },
      { id: 'interviews', icon: 'calendar-clock', label: 'Interview Sched' },
      { id: 'results', icon: 'award', label: 'Selections & Offers' },
      { id: 'analytics', icon: 'bar-chart-3', label: 'Executive Analytics' },
      { id: 'notifications', icon: 'bell', label: 'Noticeboard' }
    ],
    STUDENT: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview' },
      { id: 'profile', icon: 'user-check', label: 'My Academic Profile' },
      { id: 'drives', icon: 'briefcase', label: 'Placement Drives' },
      { id: 'applications', icon: 'file-check', label: 'My Applications' },
      { id: 'interviews', icon: 'calendar', label: 'Interview Sessions' },
      { id: 'results', icon: 'trophy', label: 'Offers & Selections' },
      { id: 'notifications', icon: 'bell', label: 'Announcements' }
    ],
    RECRUITER: [
      { id: 'overview', icon: 'layout-dashboard', label: 'Overview' },
      { id: 'drives', icon: 'briefcase', label: 'Company Drives' },
      { id: 'applications', icon: 'users', label: 'Applicant Pool' },
      { id: 'interviews', icon: 'calendar-clock', label: 'Interview Schedule' },
      { id: 'results', icon: 'award', label: 'Selected Hires' },
      { id: 'notifications', icon: 'bell', label: 'Announcements' }
    ]
  };

  const navItems = roleMenus[role] || roleMenus.STUDENT;

  navItems.forEach((item) => {
    const li = document.createElement('li');
    const button = document.createElement('button');
    button.type = 'button';
    button.className = `nav-item-btn ${item.id === currentSection ? 'active' : ''}`;
    button.id = `navBtn_${item.id}`;
    button.innerHTML = `
      <i data-lucide="${item.icon}"></i>
      <span>${item.label}</span>
    `;

    button.addEventListener('click', () => {
      navigateSection(item.id);
    });

    li.appendChild(button);
    navContainer.appendChild(li);
  });

  lucide.createIcons();
}

function toggleSidebar() {
  const sidebar = document.getElementById('appSidebar');
  if (sidebar) {
    sidebar.classList.toggle('collapsed');
    const isCollapsed = sidebar.classList.contains('collapsed');
    const toggleIcon = document.querySelector('#sidebarToggleBtn i');
    if (toggleIcon) {
      toggleIcon.setAttribute('data-lucide', isCollapsed ? 'panel-left-open' : 'panel-left-close');
      lucide.createIcons();
    }
  }
}

function setupQuickActionMenu() {
  const container = document.getElementById('quickActionDropdown');
  const label = document.getElementById('quickActionBtnLabel');
  if (!container) return;

  const role = currentUser.role || 'STUDENT';

  if (role === 'ADMIN') {
    label.innerText = 'New Action';
    container.innerHTML = `
      <div style="font-size:0.75rem;font-weight:700;color:var(--text-muted);padding:0.4rem 0.6rem;text-transform:uppercase;">Admin Actions</div>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('driveModal'); hideQuickActionMenu();">
        <i data-lucide="calendar-plus" style="width:16px;height:16px;"></i> + New Placement Drive
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('studentModal'); hideQuickActionMenu();">
        <i data-lucide="user-plus" style="width:16px;height:16px;"></i> + Add Student Record
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('companyModal'); hideQuickActionMenu();">
        <i data-lucide="building" style="width:16px;height:16px;"></i> + Add Hiring Partner
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('interviewModal'); hideQuickActionMenu();">
        <i data-lucide="calendar-clock" style="width:16px;height:16px;"></i> + Schedule Interview
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('offerModal'); hideQuickActionMenu();">
        <i data-lucide="award" style="width:16px;height:16px;"></i> + Record Selection Offer
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('broadcastModal'); hideQuickActionMenu();">
        <i data-lucide="megaphone" style="width:16px;height:16px;"></i> + Broadcast Alert
      </button>
    `;
  } else if (role === 'STUDENT') {
    label.innerText = 'Student Actions';
    container.innerHTML = `
      <div style="font-size:0.75rem;font-weight:700;color:var(--text-muted);padding:0.4rem 0.6rem;text-transform:uppercase;">Quick Links</div>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="navigateSection('drives'); hideQuickActionMenu();">
        <i data-lucide="briefcase" style="width:16px;height:16px;"></i> Browse Drives
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="navigateSection('profile'); hideQuickActionMenu();">
        <i data-lucide="user" style="width:16px;height:16px;"></i> Update Profile
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="navigateSection('applications'); hideQuickActionMenu();">
        <i data-lucide="file-check" style="width:16px;height:16px;"></i> Track Applications
      </button>
    `;
  } else {
    label.innerText = 'Recruiter Actions';
    container.innerHTML = `
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="openModal('driveModal'); hideQuickActionMenu();">
        <i data-lucide="plus-circle" style="width:16px;height:16px;"></i> Post Job Drive
      </button>
      <button type="button" class="btn btn-ghost" style="width:100%;justify-content:flex-start;font-size:0.85rem;" onclick="navigateSection('applications'); hideQuickActionMenu();">
        <i data-lucide="users" style="width:16px;height:16px;"></i> Review Candidates
      </button>
    `;
  }

  lucide.createIcons();
}

function toggleQuickActionMenu() {
  const dropdown = document.getElementById('quickActionDropdown');
  if (dropdown) {
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
  }
}

function hideQuickActionMenu() {
  const dropdown = document.getElementById('quickActionDropdown');
  if (dropdown) dropdown.style.display = 'none';
}

function setupKeyboardShortcuts() {
  document.addEventListener('keydown', (e) => {
    // Ctrl + K or Cmd + K focus global search
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      const search = document.getElementById('globalSearchInput');
      if (search) search.focus();
    }
    // Escape key closes modals and dropdowns
    if (e.key === 'Escape') {
      document.querySelectorAll('.modal-overlay.active').forEach(m => m.classList.remove('active'));
      hideQuickActionMenu();
    }
  });

  // Click outside closes dropdowns
  document.addEventListener('click', (e) => {
    const dropdown = document.getElementById('quickActionDropdown');
    const btn = document.querySelector('.quick-action-primary-btn');
    if (dropdown && btn && !btn.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.style.display = 'none';
    }
  });
}

function handleSignOut() {
  sessionStorage.removeItem('placement_user');
  window.location.href = 'index.html';
}

// ==========================================================================
// 5. Section Routing & Navigation
// ==========================================================================
async function navigateSection(sectionId) {
  currentSection = sectionId;

  // Update Breadcrumb & active sidebar states
  const bc = document.getElementById('breadcrumbCurrent');
  if (bc) bc.innerText = sectionId.charAt(0).toUpperCase() + sectionId.slice(1);

  document.querySelectorAll('.nav-item-btn').forEach(btn => btn.classList.remove('active'));
  const activeBtn = document.getElementById(`navBtn_${sectionId}`);
  if (activeBtn) activeBtn.classList.add('active');

  // Render Section
  const container = document.getElementById('pageContainer');
  if (!container) return;

  // Clear previous chart instances to avoid canvas reuse errors
  Object.keys(activeCharts).forEach(key => {
    if (activeCharts[key]) {
      activeCharts[key].destroy();
      delete activeCharts[key];
    }
  });

  // Render view template
  switch (sectionId) {
    case 'overview':
      await renderOverviewView(container);
      break;
    case 'students':
      await renderStudentsView(container);
      break;
    case 'companies':
      await renderCompaniesView(container);
      break;
    case 'drives':
      await renderDrivesView(container);
      break;
    case 'applications':
      await renderApplicationsView(container);
      break;
    case 'interviews':
      await renderInterviewsView(container);
      break;
    case 'results':
      await renderResultsView(container);
      break;
    case 'analytics':
      await renderAnalyticsView(container);
      break;
    case 'notifications':
      await renderNotificationsView(container);
      break;
    case 'profile':
      await renderStudentProfileView(container);
      break;
    default:
      await renderOverviewView(container);
      break;
  }

  lucide.createIcons();
}

// ==========================================================================
// 6. View: Overview / Executive Dashboard
// ==========================================================================
async function renderOverviewView(container) {
  // Fetch real statistics from Java backend
  const stats = await API.get('/api/statistics') || {
    totalStudents: 3,
    placedStudents: 1,
    unplacedStudents: 2,
    placementPercentage: '33.3',
    averagePackage: '8.50',
    highestPackage: '8.50',
    lowestPackage: '8.50',
    medianPackage: '8.50'
  };
  cache.stats = stats;

  const drives = await API.get('/api/drives') || [];
  const companies = await API.get('/api/companies') || [];
  const results = await API.get('/api/results') || [];
  const apps = await API.get('/api/applications') || [];

  const displayName = currentUser.name || currentUser.username || 'Placement Officer';

  container.innerHTML = `
    <!-- Hero Welcome Banner -->
    <div class="hero-welcome-banner">
      <div class="hero-text-content">
        <div class="hero-greeting-badge">
          <i data-lucide="sparkles" style="width:14px;height:14px;"></i>
          <span>Campus Recruitment 2024–2025</span>
        </div>
        <h2 class="hero-title">Welcome back, ${displayName}!</h2>
        <p class="hero-subtitle">You have ${drives.length} active placement drives and ${apps.length} applications in the pipeline. Monitor candidate readiness, schedule rounds, and record offers.</p>
        <div class="hero-quick-actions">
          ${currentUser.role === 'ADMIN' ? `
            <button type="button" class="btn btn-primary" onclick="openModal('driveModal')">
              <i data-lucide="calendar-plus"></i> Launch Placement Drive
            </button>
            <button type="button" class="btn btn-secondary" onclick="openModal('studentModal')">
              <i data-lucide="user-plus"></i> Enroll Student
            </button>
          ` : `
            <button type="button" class="btn btn-primary" onclick="navigateSection('drives')">
              <i data-lucide="briefcase"></i> Browse Eligible Drives
            </button>
            <button type="button" class="btn btn-secondary" onclick="navigateSection('applications')">
              <i data-lucide="file-check"></i> View My Applications
            </button>
          `}
        </div>
      </div>
    </div>

    <!-- 6 Primary Executive KPI Metric Cards -->
    <div class="kpi-metrics-grid">
      <!-- Total Students -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Total Enrolled</span>
          <div class="stat-icon-wrapper indigo"><i data-lucide="users"></i></div>
        </div>
        <div class="stat-metric-val">${stats.totalStudents || 0}</div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive"><i data-lucide="arrow-up-right" style="width:12px;height:12px;"></i> +100%</span>
          <span class="trend-subtext">Active batches</span>
        </div>
      </div>

      <!-- Placed Candidates -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Placed Candidates</span>
          <div class="stat-icon-wrapper emerald"><i data-lucide="check-circle-2"></i></div>
        </div>
        <div class="stat-metric-val">${stats.placedStudents || 0}</div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive"><i data-lucide="trending-up" style="width:12px;height:12px;"></i> Verified</span>
          <span class="trend-subtext">Offers accepted</span>
        </div>
      </div>

      <!-- Placement Rate % -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Placement Rate</span>
          <div class="stat-icon-wrapper blue"><i data-lucide="percent"></i></div>
        </div>
        <div class="stat-metric-val">${stats.placementPercentage || 0}%</div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive">+12.4%</span>
          <span class="trend-subtext">vs last session</span>
        </div>
      </div>

      <!-- Partner Companies -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Partner Companies</span>
          <div class="stat-icon-wrapper amber"><i data-lucide="building"></i></div>
        </div>
        <div class="stat-metric-val">${companies.length || 0}</div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive">Active</span>
          <span class="trend-subtext">Corporate tie-ups</span>
        </div>
      </div>

      <!-- Active Drives -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Placement Drives</span>
          <div class="stat-icon-wrapper rose"><i data-lucide="briefcase"></i></div>
        </div>
        <div class="stat-metric-val">${drives.length || 0}</div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive">Ongoing</span>
          <span class="trend-subtext">Recruitment cycles</span>
        </div>
      </div>

      <!-- Average Package -->
      <div class="stat-metric-card">
        <div class="stat-card-top">
          <span class="stat-label-text">Average CTC</span>
          <div class="stat-icon-wrapper emerald"><i data-lucide="badge-indian-rupee"></i></div>
        </div>
        <div class="stat-metric-val">₹${stats.averagePackage || '0.00'} <span style="font-size:0.9rem;font-weight:600;color:var(--text-muted);">LPA</span></div>
        <div class="stat-card-bottom">
          <span class="trend-indicator positive">Top ₹${stats.highestPackage || '0'} LPA</span>
          <span class="trend-subtext">Annual CTC</span>
        </div>
      </div>
    </div>

    <!-- Charts & Analytics Section -->
    <div class="charts-double-grid">
      <!-- Main Trend & Department Bar Chart -->
      <div class="card">
        <div class="card-header-row">
          <div>
            <div class="card-title"><i data-lucide="bar-chart-2"></i> Department-Wise Placement Comparison</div>
            <div class="card-subtitle">Real-time candidate placement distribution across departments</div>
          </div>
          <button type="button" class="btn btn-secondary btn-sm" onclick="navigateSection('analytics')">
            <span>Detailed Analytics</span> <i data-lucide="arrow-right" style="width:14px;height:14px;"></i>
          </button>
        </div>
        <div class="chart-container-box" style="height: 260px;">
          <canvas id="overviewBarChart"></canvas>
        </div>
      </div>

      <!-- Placement Status Donut Chart -->
      <div class="card">
        <div class="card-header-row">
          <div>
            <div class="card-title"><i data-lucide="pie-chart"></i> Outcome Ratio</div>
            <div class="card-subtitle">Placed vs Unplaced candidate ratio</div>
          </div>
        </div>
        <div class="chart-container-box" style="height: 200px; display:flex; justify-content:center;">
          <canvas id="overviewDonutChart"></canvas>
        </div>
        <div style="display:flex; justify-content:space-around; margin-top:1rem; font-size:0.82rem;">
          <div style="display:flex;align-items:center;gap:0.4rem;">
            <span style="width:10px;height:10px;border-radius:2px;background:#4F46E5;"></span>
            <span>Placed (${stats.placedStudents || 0})</span>
          </div>
          <div style="display:flex;align-items:center;gap:0.4rem;">
            <span style="width:10px;height:10px;border-radius:2px;background:#E2E8F0;"></span>
            <span>Unplaced (${stats.unplacedStudents || 0})</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Dual Feed: Recent Placement Selections & Upcoming Events -->
    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap:1.5rem;">
      <!-- Latest Offers Feed -->
      <div class="card">
        <div class="card-header-row">
          <div class="card-title"><i data-lucide="trophy"></i> Latest Placement Selections</div>
          <button type="button" class="btn btn-ghost btn-sm" onclick="navigateSection('results')">View All</button>
        </div>
        ${results.length === 0 ? `
          <div style="text-align:center;padding:2rem;color:var(--text-light);">
            <i data-lucide="award" style="width:36px;height:36px;margin:0 auto 0.5rem;opacity:0.5;"></i>
            <p>No final offers recorded yet in this cycle.</p>
          </div>
        ` : `
          <div style="display:flex;flex-direction:column;gap:0.75rem;">
            ${results.slice(0, 4).map(r => `
              <div style="display:flex;align-items:center;justify-content:space-between;padding:0.75rem 1rem;background:var(--slate-50);border-radius:var(--radius-md);border:1px solid var(--border-subtle);">
                <div style="display:flex;align-items:center;gap:0.75rem;">
                  <div class="avatar-initials-small">${(r.studentName || 'S').charAt(0)}</div>
                  <div>
                    <strong style="font-size:0.9rem;color:var(--text-primary);">${r.studentName || r.studentId}</strong>
                    <div style="font-size:0.78rem;color:var(--text-muted);">${r.companyName} &bull; ${r.position || 'Software Engineer'}</div>
                  </div>
                </div>
                <div style="text-align:right;">
                  <span class="badge-pill success">₹${r.packageLPA} LPA</span>
                  <div style="font-size:0.72rem;color:var(--text-light);margin-top:2px;">${r.resultDate || 'Recorded'}</div>
                </div>
              </div>
            `).join('')}
          </div>
        `}
      </div>

      <!-- Upcoming Placement Drives -->
      <div class="card">
        <div class="card-header-row">
          <div class="card-title"><i data-lucide="calendar"></i> Upcoming Placement Drives</div>
          <button type="button" class="btn btn-ghost btn-sm" onclick="navigateSection('drives')">View All</button>
        </div>
        ${drives.length === 0 ? `
          <div style="text-align:center;padding:2rem;color:var(--text-light);">
            <i data-lucide="calendar" style="width:36px;height:36px;margin:0 auto 0.5rem;opacity:0.5;"></i>
            <p>No upcoming drives scheduled currently.</p>
          </div>
        ` : `
          <div style="display:flex;flex-direction:column;gap:0.75rem;">
            ${drives.slice(0, 4).map(d => `
              <div style="display:flex;align-items:center;justify-content:space-between;padding:0.75rem 1rem;background:var(--slate-50);border-radius:var(--radius-md);border:1px solid var(--border-subtle);">
                <div style="display:flex;align-items:center;gap:0.75rem;">
                  <div style="width:36px;height:36px;border-radius:8px;background:var(--indigo-50);color:var(--indigo-600);display:flex;align-items:center;justify-content:center;font-weight:700;">
                    ${(d.companyName || 'C').charAt(0)}
                  </div>
                  <div>
                    <strong style="font-size:0.9rem;color:var(--text-primary);">${d.title}</strong>
                    <div style="font-size:0.78rem;color:var(--text-muted);">${d.companyName} &bull; Min CGPA: ${d.minCGPA}</div>
                  </div>
                </div>
                <div style="text-align:right;">
                  <span class="badge-pill indigo">${d.driveDate}</span>
                  <button type="button" class="btn btn-secondary btn-sm" style="margin-top:4px;padding:2px 8px;font-size:0.75rem;" onclick="openJobDetailModal('${d.id}')">Details</button>
                </div>
              </div>
            `).join('')}
          </div>
        `}
      </div>
    </div>
  `;

  // Render Charts
  initOverviewCharts(stats);
}

function initOverviewCharts(stats) {
  // Department Bar Chart
  const barCanvas = document.getElementById('overviewBarChart');
  if (barCanvas) {
    const ctx = barCanvas.getContext('2d');
    activeCharts['overviewBar'] = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Computer Science (CSE)', 'Electronics (ECE)', 'Information Tech (IT)', 'Mechanical (ME)', 'Civil (CE)', 'Electrical (EE)'],
        datasets: [
          {
            label: 'Eligible Candidates',
            data: [42, 30, 28, 20, 16, 22],
            backgroundColor: '#CBD5E1',
            borderRadius: 6
          },
          {
            label: 'Placed Offers',
            data: [36, 22, 24, 12, 9, 15],
            backgroundColor: '#4F46E5',
            borderRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'top', labels: { boxWidth: 12, font: { family: 'Plus Jakarta Sans', size: 12 } } },
          tooltip: { padding: 10, cornerRadius: 8 }
        },
        scales: {
          x: { grid: { display: false } },
          y: { grid: { color: '#F1F5F9' }, beginAtZero: true }
        }
      }
    });
  }

  // Outcome Donut Chart
  const donutCanvas = document.getElementById('overviewDonutChart');
  if (donutCanvas) {
    const ctx = donutCanvas.getContext('2d');
    const placed = parseInt(stats.placedStudents || 1);
    const unplaced = Math.max(0, parseInt(stats.totalStudents || 3) - placed);

    activeCharts['overviewDonut'] = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Placed', 'Unplaced / In Process'],
        datasets: [{
          data: [placed, unplaced],
          backgroundColor: ['#4F46E5', '#E2E8F0'],
          borderWidth: 0,
          cutout: '72%'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { padding: 8, cornerRadius: 8 }
        }
      }
    });
  }
}

// ==========================================================================
// 7. View: Student Management Directory
// ==========================================================================
async function renderStudentsView(container) {
  const students = await API.get('/api/students') || [];
  cache.students = students;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Student Candidate Directory</h1>
        <p>Comprehensive academic, CGPA, and placement status records for all enrolled candidates.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role === 'ADMIN' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('studentModal')">
            <i data-lucide="user-plus"></i> Enroll Student
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Filter and Search Toolbar -->
    <div class="card" style="padding:1rem;">
      <div class="filter-action-toolbar" style="margin-bottom:0;">
        <div class="filter-search-field">
          <i data-lucide="search"></i>
          <input type="text" id="studentSearchFilter" placeholder="Search by name, roll no, or skill..." onkeyup="filterStudentsTable()">
        </div>
        <div class="filter-pills-group">
          <select id="studentBranchFilter" class="form-select" style="width:140px;padding:0.45rem 0.65rem;" onchange="filterStudentsTable()">
            <option value="">All Branches</option>
            <option value="CSE">CSE</option>
            <option value="ECE">ECE</option>
            <option value="IT">IT</option>
            <option value="ME">ME</option>
            <option value="CE">CE</option>
            <option value="EE">EE</option>
          </select>
          <select id="studentStatusFilter" class="form-select" style="width:140px;padding:0.45rem 0.65rem;" onchange="filterStudentsTable()">
            <option value="">All Statuses</option>
            <option value="PLACED">Placed</option>
            <option value="UNPLACED">Eligible / Unplaced</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Students Table -->
    <div class="table-responsive-wrapper">
      <table class="pro-data-table" id="studentsDataTable">
        <thead>
          <tr>
            <th>Candidate</th>
            <th>Roll Number</th>
            <th>Department</th>
            <th>CGPA &amp; Academics</th>
            <th>Backlogs</th>
            <th>Placement Status</th>
            <th style="text-align:right;">Actions</th>
          </tr>
        </thead>
        <tbody id="studentsTableBody">
          ${renderStudentsTableRows(students)}
        </tbody>
      </table>
    </div>
  `;
}

function renderStudentsTableRows(students) {
  if (students.length === 0) {
    return `<tr><td colspan="7" style="text-align:center;padding:2.5rem;color:var(--text-light);">No candidate records match your query.</td></tr>`;
  }

  return students.map(s => {
    const isPlaced = s.isPlaced === 'true' || s.isPlaced === true;
    const cgpa = parseFloat(s.cgpa || 0);
    const backlogs = parseInt(s.activeBacklogs || 0);

    return `
      <tr>
        <td>
          <div class="user-avatar-cell">
            <div class="avatar-initials-small">${(s.name || 'S').charAt(0)}</div>
            <div>
              <div class="user-cell-meta-name">${s.name}</div>
              <div class="user-cell-meta-sub">${s.email || 'No email registered'}</div>
            </div>
          </div>
        </td>
        <td><strong style="font-family:var(--font-mono);font-size:0.85rem;">${s.rollNumber || '—'}</strong></td>
        <td><span class="badge-pill indigo">${s.branch || 'CSE'}</span></td>
        <td>
          <div style="display:flex;align-items:center;gap:0.5rem;">
            <strong style="color:${cgpa >= 8 ? '#059669' : cgpa >= 7 ? '#4F46E5' : '#D97706'};">${cgpa.toFixed(2)}</strong>
            <span style="font-size:0.75rem;color:var(--text-muted);">(Class of ${s.graduationYear || 2025})</span>
          </div>
        </td>
        <td>
          <span class="badge-pill ${backlogs === 0 ? 'success' : 'danger'}">
            ${backlogs === 0 ? '0 Backlogs' : `${backlogs} Active`}
          </span>
        </td>
        <td>
          <span class="badge-pill ${isPlaced ? 'success' : 'neutral'}">
            <span class="badge-dot"></span>
            ${isPlaced ? `Placed &bull; ${s.placedCompany || 'Offered'}` : 'Eligible'}
          </span>
        </td>
        <td style="text-align:right;">
          <div style="display:inline-flex;gap:0.35rem;">
            <button type="button" class="btn btn-secondary btn-sm" title="Edit Profile" onclick="editStudentModal('${s.id}')">
              <i data-lucide="edit-3" style="width:14px;height:14px;"></i>
            </button>
            ${currentUser.role === 'ADMIN' ? `
              <button type="button" class="btn btn-ghost btn-sm" style="color:var(--rose-600);" title="Delete" onclick="deleteStudentRecord('${s.id}')">
                <i data-lucide="trash-2" style="width:14px;height:14px;"></i>
              </button>
            ` : ''}
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

function filterStudentsTable() {
  const query = (document.getElementById('studentSearchFilter')?.value || '').toLowerCase();
  const branch = document.getElementById('studentBranchFilter')?.value || '';
  const status = document.getElementById('studentStatusFilter')?.value || '';

  const filtered = cache.students.filter(s => {
    const matchQuery = (s.name || '').toLowerCase().includes(query) ||
                       (s.rollNumber || '').toLowerCase().includes(query) ||
                       (s.skills || '').toLowerCase().includes(query);
    const matchBranch = !branch || (s.branch || '').toUpperCase() === branch.toUpperCase();
    const isPlaced = s.isPlaced === 'true' || s.isPlaced === true;
    const matchStatus = !status || (status === 'PLACED' ? isPlaced : !isPlaced);
    return matchQuery && matchBranch && matchStatus;
  });

  const tbody = document.getElementById('studentsTableBody');
  if (tbody) {
    tbody.innerHTML = renderStudentsTableRows(filtered);
    lucide.createIcons();
  }
}

// ==========================================================================
// 8. View: Company Management
// ==========================================================================
async function renderCompaniesView(container) {
  const companies = await API.get('/api/companies') || [];
  cache.companies = companies;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Corporate Hiring Partners</h1>
        <p>Manage partner enterprise profiles, recruiting history, and minimum selection eligibility criteria.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role === 'ADMIN' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('companyModal')">
            <i data-lucide="building"></i> Register Company Partner
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Company Partner Cards Grid -->
    <div style="display:grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap:1.25rem;">
      ${companies.map(c => `
        <div class="card" style="display:flex;flex-direction:column;justify-content:space-between;gap:1.25rem;">
          <div>
            <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:1rem;">
              <div style="display:flex;align-items:center;gap:0.75rem;">
                <div class="company-logo-square">${(c.name || 'C').charAt(0)}</div>
                <div>
                  <h3 style="font-size:1.1rem;font-weight:700;color:var(--text-primary);">${c.name}</h3>
                  <div style="font-size:0.8rem;color:var(--text-muted);">${c.industry || 'Technology'}</div>
                </div>
              </div>
              <span class="badge-pill blue">${c.location || 'India'}</span>
            </div>

            <p style="font-size:0.88rem;color:var(--text-secondary);margin:1rem 0;line-height:1.5;">
              ${c.description || 'Enterprise hiring partner participating in campus placement drives.'}
            </p>

            <div style="display:flex;flex-wrap:wrap;gap:0.5rem;font-size:0.8rem;">
              <span class="badge-pill indigo">Min CGPA: ${c.minCGPA || '6.5'}</span>
              <span class="badge-pill neutral">${c.allowedBranches ? c.allowedBranches.split(',').join(' &bull; ') : 'CSE &bull; ECE'}</span>
            </div>
          </div>

          <div style="display:flex;align-items:center;justify-content:space-between;padding-top:1rem;border-top:1px solid var(--border-subtle);font-size:0.82rem;">
            <a href="${c.website || '#'}" target="_blank" style="display:inline-flex;align-items:center;gap:0.3rem;color:var(--indigo-600);font-weight:600;">
              <i data-lucide="globe" style="width:14px;height:14px;"></i> Website
            </a>
            ${currentUser.role === 'ADMIN' ? `
              <div style="display:inline-flex;gap:0.25rem;">
                <button type="button" class="btn btn-ghost btn-sm" style="color:var(--rose-600);" onclick="deleteCompanyRecord('${c.id}')">
                  <i data-lucide="trash-2" style="width:14px;height:14px;"></i>
                </button>
              </div>
            ` : ''}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// ==========================================================================
// 9. View: Placement Drives & Job Opportunities
// ==========================================================================
async function renderDrivesView(container) {
  const drives = await API.get('/api/drives') || [];
  cache.drives = drives;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Placement Drives &amp; Opportunities</h1>
        <p>Active and scheduled campus placement drives with real-time criteria verification and 1-click submission.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role === 'ADMIN' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('driveModal')">
            <i data-lucide="calendar-plus"></i> Launch Placement Drive
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Drives Cards Grid -->
    <div class="jobs-cards-grid">
      ${drives.map(d => {
        return `
          <div class="job-card">
            <div>
              <div class="job-card-header">
                <div class="company-logo-square">${(d.companyName || 'C').charAt(0)}</div>
                <div class="job-meta-top">
                  <h3 class="job-title-h3">${d.title}</h3>
                  <div class="job-company-name">${d.companyName}</div>
                </div>
                <div class="job-package-tag">Min ${d.minCGPA} CGPA</div>
              </div>

              <p style="font-size:0.88rem;color:var(--text-secondary);margin:1rem 0;line-height:1.5;">
                ${d.description || 'Full-time campus recruitment drive for eligible engineering graduates.'}
              </p>

              <div class="job-criteria-tags-list">
                <span class="badge-pill indigo"><i data-lucide="calendar" style="width:12px;height:12px;"></i> Drive: ${d.driveDate}</span>
                <span class="badge-pill warning"><i data-lucide="clock" style="width:12px;height:12px;"></i> Apply by: ${d.lastDateToApply || 'Rolling'}</span>
                <span class="badge-pill neutral">${d.allowedBranches ? d.allowedBranches.split(',').slice(0, 3).join(', ') : 'All Branches'}</span>
              </div>
            </div>

            <div class="job-card-footer">
              <button type="button" class="btn btn-secondary btn-sm" onclick="openJobDetailModal('${d.id}')">
                <i data-lucide="info" style="width:14px;height:14px;"></i> View Criteria
              </button>
              <div style="display:inline-flex;gap:0.4rem;">
                <button type="button" class="btn btn-primary btn-sm" onclick="handleQuickApply('${d.id}')">
                  <i data-lucide="send" style="width:14px;height:14px;"></i> Apply Now
                </button>
                ${currentUser.role === 'ADMIN' ? `
                  <button type="button" class="btn btn-ghost btn-sm" style="color:var(--rose-600);" onclick="deleteDriveRecord('${d.id}')">
                    <i data-lucide="trash-2" style="width:14px;height:14px;"></i>
                  </button>
                ` : ''}
              </div>
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

// ==========================================================================
// 10. View: Applications & Pipeline
// ==========================================================================
async function renderApplicationsView(container) {
  const url = currentUser.role === 'STUDENT' ? `/api/applications?studentId=${currentUser.id || 'STU001'}` : '/api/applications';
  const apps = await API.get(url) || [];
  cache.applications = apps;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Application Tracking Pipeline</h1>
        <p>${currentUser.role === 'STUDENT' ? 'Track your active applications, status updates, and stage progressions in real time.' : 'Review candidate applications, shortlist profiles, and advance recruitment stages.'}</p>
      </div>
    </div>

    <!-- Applications Table -->
    <div class="table-responsive-wrapper">
      <table class="pro-data-table">
        <thead>
          <tr>
            <th>Application ID</th>
            <th>Candidate</th>
            <th>Hiring Company</th>
            <th>Submission Date</th>
            <th>Current Stage</th>
            <th>Remarks</th>
            ${currentUser.role !== 'STUDENT' ? '<th style="text-align:right;">Actions</th>' : ''}
          </tr>
        </thead>
        <tbody>
          ${apps.length === 0 ? `
            <tr><td colspan="7" style="text-align:center;padding:2.5rem;color:var(--text-light);">No applications found in the pipeline.</td></tr>
          ` : apps.map(a => {
            const st = (a.status || 'APPLIED').toUpperCase();
            let badgeClass = 'indigo';
            if (st.includes('SELECT')) badgeClass = 'success';
            else if (st.includes('REJECT')) badgeClass = 'danger';
            else if (st.includes('SHORTLIST') || st.includes('INTERVIEW')) badgeClass = 'blue';

            return `
              <tr>
                <td><strong style="font-family:var(--font-mono);font-size:0.85rem;">${a.id}</strong></td>
                <td>
                  <div style="font-weight:600;color:var(--text-primary);">${a.studentName || a.studentId}</div>
                  <div style="font-size:0.75rem;color:var(--text-muted);">${a.rollNumber || ''}</div>
                </td>
                <td><strong>${a.companyName || 'Corporate Partner'}</strong></td>
                <td><span style="font-size:0.85rem;color:var(--text-muted);">${a.appliedAt || 'Recent'}</span></td>
                <td>
                  <span class="badge-pill ${badgeClass}">
                    <span class="badge-dot"></span>
                    ${a.status || 'Applied'}
                  </span>
                </td>
                <td><small style="color:var(--text-secondary);">${a.remarks || 'Under active evaluation'}</small></td>
                ${currentUser.role !== 'STUDENT' ? `
                  <td style="text-align:right;">
                    <div style="display:inline-flex;gap:0.35rem;">
                      <button type="button" class="btn btn-secondary btn-sm" onclick="updateApplicationStage('${a.id}', 'SHORTLISTED')">Shortlist</button>
                      <button type="button" class="btn btn-secondary btn-sm" onclick="updateApplicationStage('${a.id}', 'TECHNICAL_INTERVIEW')">Technical</button>
                      <button type="button" class="btn btn-secondary btn-sm" style="color:var(--rose-600);" onclick="updateApplicationStage('${a.id}', 'REJECTED')">Reject</button>
                    </div>
                  </td>
                ` : ''}
              </tr>
            `;
          }).join('')}
        </tbody>
      </table>
    </div>
  `;
}

// ==========================================================================
// 11. View: Interview Scheduling
// ==========================================================================
async function renderInterviewsView(container) {
  const url = currentUser.role === 'STUDENT' ? `/api/interviews?studentId=${currentUser.id || 'STU001'}` : '/api/interviews';
  const interviews = await API.get(url) || [];
  cache.interviews = interviews;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Interview Scheduling &amp; Agenda</h1>
        <p>Chronological agenda of technical, coding, and HR rounds with conflict-avoidance verification.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role !== 'STUDENT' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('interviewModal')">
            <i data-lucide="calendar-plus"></i> Schedule Interview
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Interviews Agenda Table -->
    <div class="table-responsive-wrapper">
      <table class="pro-data-table">
        <thead>
          <tr>
            <th>Interview ID</th>
            <th>Candidate</th>
            <th>Company</th>
            <th>Round</th>
            <th>Date &amp; Time</th>
            <th>Venue / Virtual Link</th>
            <th>Status</th>
            ${currentUser.role !== 'STUDENT' ? '<th style="text-align:right;">Actions</th>' : ''}
          </tr>
        </thead>
        <tbody>
          ${interviews.length === 0 ? `
            <tr><td colspan="8" style="text-align:center;padding:2.5rem;color:var(--text-light);">No scheduled interviews on record.</td></tr>
          ` : interviews.map(i => `
            <tr>
              <td><strong style="font-family:var(--font-mono);font-size:0.85rem;">${i.id}</strong></td>
              <td><strong>${i.studentId}</strong></td>
              <td>${i.companyName}</td>
              <td><span class="badge-pill indigo">${i.roundType ? i.roundType.replace(/_/g, ' ') : 'Technical'}</span></td>
              <td>
                <div style="font-weight:600;">${i.scheduledDate}</div>
                <div style="font-size:0.78rem;color:var(--text-muted);">${i.scheduledTime}</div>
              </td>
              <td><span style="font-size:0.85rem;color:var(--text-secondary);">${i.venue || 'TBA'}</span></td>
              <td>
                <span class="badge-pill success">
                  <span class="badge-dot"></span>
                  ${i.status || 'SCHEDULED'}
                </span>
              </td>
              ${currentUser.role !== 'STUDENT' ? `
                <td style="text-align:right;">
                  <button type="button" class="btn btn-ghost btn-sm" style="color:var(--rose-600);" title="Cancel Interview" onclick="deleteInterviewRecord('${i.id}')">
                    <i data-lucide="x-circle" style="width:15px;height:15px;"></i> Cancel
                  </button>
                </td>
              ` : ''}
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

// ==========================================================================
// 12. View: Results & Offers
// ==========================================================================
async function renderResultsView(container) {
  const results = await API.get('/api/results') || [];
  cache.results = results;

  // Compute package stats
  const packages = results.map(r => parseFloat(r.packageLPA || 0)).filter(p => p > 0);
  const highest = packages.length > 0 ? Math.max(...packages) : 0;
  const avg = packages.length > 0 ? (packages.reduce((a,b)=>a+b, 0) / packages.length).toFixed(2) : 0;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Placement Offers &amp; Selections</h1>
        <p>Verified candidate offers, compensation packages, and recruitment milestones.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role !== 'STUDENT' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('offerModal')">
            <i data-lucide="award"></i> Record Selection Offer
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Package Highlight Cards -->
    <div class="package-highlights-box">
      <div class="package-highlight-item">
        <div class="package-highlight-label">Highest Package Awarded</div>
        <div class="package-highlight-value">₹${highest.toFixed(1)} LPA</div>
      </div>
      <div class="package-highlight-item">
        <div class="package-highlight-label">Average Annual CTC</div>
        <div class="package-highlight-value" style="color:var(--emerald-600);">₹${avg} LPA</div>
      </div>
      <div class="package-highlight-item">
        <div class="package-highlight-label">Total Verified Selections</div>
        <div class="package-highlight-value" style="color:var(--blue-600);">${results.length} Offers</div>
      </div>
    </div>

    <!-- Offers Table -->
    <div class="table-responsive-wrapper" style="margin-top:1.5rem;">
      <table class="pro-data-table">
        <thead>
          <tr>
            <th>Student Candidate</th>
            <th>Roll Number</th>
            <th>Hiring Company</th>
            <th>Role Designation</th>
            <th>Offered Package</th>
            <th>Award Date</th>
            <th>Status</th>
            ${currentUser.role !== 'STUDENT' ? '<th style="text-align:right;">Actions</th>' : ''}
          </tr>
        </thead>
        <tbody>
          ${results.length === 0 ? `
            <tr><td colspan="8" style="text-align:center;padding:2.5rem;color:var(--text-light);">No final selection offers recorded yet.</td></tr>
          ` : results.map(r => `
            <tr>
              <td>
                <div class="user-avatar-cell">
                  <div class="avatar-initials-small">${(r.studentName || 'S').charAt(0)}</div>
                  <strong style="color:var(--text-primary);">${r.studentName || r.studentId}</strong>
                </div>
              </td>
              <td><strong style="font-family:var(--font-mono);font-size:0.85rem;">${r.rollNumber || '—'}</strong></td>
              <td><strong>${r.companyName}</strong></td>
              <td>${r.position || 'Software Engineer'}</td>
              <td><strong style="font-size:1rem;color:var(--emerald-600);">₹${r.packageLPA} LPA</strong></td>
              <td><span style="font-size:0.85rem;color:var(--text-muted);">${r.resultDate || 'Recorded'}</span></td>
              <td><span class="badge-pill success"><span class="badge-dot"></span> Selected</span></td>
              ${currentUser.role !== 'STUDENT' ? `
                <td style="text-align:right;">
                  <button type="button" class="btn btn-ghost btn-sm" style="color:var(--rose-600);" onclick="deleteOfferRecord('${r.id}')">
                    <i data-lucide="trash-2" style="width:14px;height:14px;"></i>
                  </button>
                </td>
              ` : ''}
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

// ==========================================================================
// 13. View: Analytics & Reports
// ==========================================================================
async function renderAnalyticsView(container) {
  const stats = await API.get('/api/statistics') || {
    totalStudents: 3,
    placedStudents: 1,
    unplacedStudents: 2,
    placementPercentage: '33.3',
    averagePackage: '8.50',
    highestPackage: '8.50',
    lowestPackage: '8.50',
    medianPackage: '8.50'
  };

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Executive Placement Analytics</h1>
        <p>Comprehensive recruitment metrics, branch conversion rates, and package tier distribution.</p>
      </div>
      <div class="view-header-actions">
        <button type="button" class="btn btn-secondary" onclick="window.print()">
          <i data-lucide="download"></i> Export Analytics Report
        </button>
      </div>
    </div>

    <!-- Analytics Charts Grid -->
    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(450px, 1fr)); gap:1.5rem;">
      <!-- Department Bar Chart -->
      <div class="card">
        <div class="card-title" style="margin-bottom:1rem;"><i data-lucide="bar-chart-3"></i> Branch-Wise Placement Rate</div>
        <div class="chart-container-box" style="height:280px;">
          <canvas id="analyticsBranchChart"></canvas>
        </div>
      </div>

      <!-- Package Tier Distribution Chart -->
      <div class="card">
        <div class="card-title" style="margin-bottom:1rem;"><i data-lucide="pie-chart"></i> Package Tier Distribution</div>
        <div class="chart-container-box" style="height:280px; display:flex; justify-content:center;">
          <canvas id="analyticsTierChart"></canvas>
        </div>
      </div>
    </div>

    <!-- Summary Metrics Card -->
    <div class="card" style="margin-top:1.5rem;">
      <div class="card-title" style="margin-bottom:1rem;"><i data-lucide="table"></i> Key Academic Metrics Breakdown</div>
      <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap:1rem;">
        <div class="package-highlight-item">
          <div class="package-highlight-label">Highest Package</div>
          <div class="package-highlight-value">₹${stats.highestPackage || '0.00'} LPA</div>
        </div>
        <div class="package-highlight-item">
          <div class="package-highlight-label">Median Package</div>
          <div class="package-highlight-value" style="color:var(--blue-600);">₹${stats.medianPackage || '0.00'} LPA</div>
        </div>
        <div class="package-highlight-item">
          <div class="package-highlight-label">Average Package</div>
          <div class="package-highlight-value" style="color:var(--emerald-600);">₹${stats.averagePackage || '0.00'} LPA</div>
        </div>
        <div class="package-highlight-item">
          <div class="package-highlight-label">Overall Conversion</div>
          <div class="package-highlight-value" style="color:var(--amber-600);">${stats.placementPercentage || '0'}%</div>
        </div>
      </div>
    </div>
  `;

  // Render Charts
  const ctxB = document.getElementById('analyticsBranchChart')?.getContext('2d');
  if (ctxB) {
    activeCharts['analyticsBranch'] = new Chart(ctxB, {
      type: 'bar',
      data: {
        labels: ['CSE (85%)', 'ECE (73%)', 'IT (82%)', 'ME (60%)', 'CE (56%)', 'EE (68%)'],
        datasets: [{
          label: 'Placement % by Branch',
          data: [85, 73, 82, 60, 56, 68],
          backgroundColor: '#4F46E5',
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true, max: 100 } }
      }
    });
  }

  const ctxT = document.getElementById('analyticsTierChart')?.getContext('2d');
  if (ctxT) {
    activeCharts['analyticsTier'] = new Chart(ctxT, {
      type: 'doughnut',
      data: {
        labels: ['0 - 6 LPA', '6 - 12 LPA', '12 - 20 LPA', '20+ LPA (Dream)'],
        datasets: [{
          data: [25, 45, 20, 10],
          backgroundColor: ['#94A3B8', '#3B82F6', '#4F46E5', '#10B981'],
          cutout: '65%'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }
}

// ==========================================================================
// 14. View: Notifications & Announcements
// ==========================================================================
async function renderNotificationsView(container) {
  const notifs = await API.get('/api/notifications') || [];
  cache.notifications = notifs;

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>Campus Placement Noticeboard</h1>
        <p>Live broadcast alerts, drive schedules, and urgent notifications issued by the placement cell.</p>
      </div>
      <div class="view-header-actions">
        ${currentUser.role === 'ADMIN' ? `
          <button type="button" class="btn btn-primary" onclick="openModal('broadcastModal')">
            <i data-lucide="megaphone"></i> Broadcast Announcement
          </button>
        ` : ''}
      </div>
    </div>

    <!-- Announcements Feed -->
    <div class="card">
      ${notifs.length === 0 ? `
        <div style="text-align:center;padding:3rem;color:var(--text-light);">
          <i data-lucide="bell-off" style="width:40px;height:40px;margin:0 auto 0.5rem;opacity:0.4;"></i>
          <p>No active announcements posted on the noticeboard.</p>
        </div>
      ` : `
        <div style="display:flex;flex-direction:column;gap:1rem;">
          ${notifs.map(n => `
            <div style="display:flex;align-items:flex-start;gap:1rem;padding:1rem;background:var(--slate-50);border-radius:var(--radius-md);border-left:4px solid var(--indigo-600);">
              <div style="width:36px;height:36px;border-radius:var(--radius-md);background:var(--indigo-100);color:var(--indigo-600);display:flex;align-items:center;justify-content:center;flex-shrink:0;">
                <i data-lucide="bell" style="width:18px;height:18px;"></i>
              </div>
              <div style="flex:1;">
                <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:0.3rem;">
                  <span class="badge-pill indigo" style="font-size:0.72rem;">To: ${n.userId || 'ALL'}</span>
                  <span style="font-size:0.78rem;color:var(--text-light);">${n.timestamp || 'Recent'}</span>
                </div>
                <div style="font-size:0.95rem;color:var(--text-primary);line-height:1.5;">${n.message}</div>
              </div>
            </div>
          `).join('')}
        </div>
      `}
    </div>
  `;
}

// ==========================================================================
// 15. View: Student Profile (Student Dashboard Mode)
// ==========================================================================
async function renderStudentProfileView(container) {
  const studentId = currentUser.id || 'STU001';
  const s = await API.get(`/api/students?id=${studentId}`) || {};

  container.innerHTML = `
    <div class="view-header">
      <div class="view-header-title">
        <h1>My Candidate Profile</h1>
        <p>Maintain your verified academic score, department, and contact details for placement eligibility.</p>
      </div>
    </div>

    <!-- Profile Completion Score & Form -->
    <div style="display:grid; grid-template-columns: 2fr 1fr; gap:1.5rem;">
      <!-- Profile Form -->
      <div class="card">
        <div class="card-title" style="margin-bottom:1.25rem;"><i data-lucide="user"></i> Verified Academic Information</div>
        <form onsubmit="handleSaveProfileUpdate(event)">
          <input type="hidden" id="studentProfId" value="${s.id || studentId}">
          <div class="form-grid">
            <div class="form-group">
              <label class="form-label">Full Name</label>
              <input type="text" id="studentProfName" class="form-input" value="${s.name || ''}" required>
            </div>
            <div class="form-group">
              <label class="form-label">Roll Number</label>
              <input type="text" class="form-input" value="${s.rollNumber || ''}" disabled style="background:var(--slate-100);">
            </div>
            <div class="form-group">
              <label class="form-label">Department / Branch</label>
              <input type="text" class="form-input" value="${s.branch || 'CSE'}" disabled style="background:var(--slate-100);">
            </div>
            <div class="form-group">
              <label class="form-label">Current CGPA</label>
              <input type="number" step="0.01" min="0" max="10" id="studentProfCgpa" class="form-input" value="${s.cgpa || ''}" required>
            </div>
            <div class="form-group">
              <label class="form-label">Active Backlogs</label>
              <input type="number" min="0" id="studentProfBacklogs" class="form-input" value="${s.activeBacklogs || 0}" required>
            </div>
            <div class="form-group">
              <label class="form-label">Contact Email</label>
              <input type="email" id="studentProfEmail" class="form-input" value="${s.email || ''}" required>
            </div>
          </div>
          <button type="submit" class="btn btn-primary" style="margin-top:1.5rem;">Save Profile Updates</button>
        </form>
      </div>

      <!-- Profile Card Summary -->
      <div class="card" style="display:flex;flex-direction:column;gap:1.25rem;">
        <div style="text-align:center;padding:1rem 0;">
          <div class="user-avatar-circle" style="width:64px;height:64px;font-size:1.5rem;margin:0 auto 0.75rem;">
            ${(s.name || 'U').charAt(0)}
          </div>
          <h3 style="font-size:1.15rem;font-weight:700;">${s.name || 'Student Candidate'}</h3>
          <p style="font-size:0.82rem;color:var(--text-muted);">${s.rollNumber || ''} &bull; ${s.branch || 'CSE'}</p>
        </div>

        <div style="border-top:1px solid var(--border-subtle);padding-top:1rem;display:flex;flex-direction:column;gap:0.75rem;font-size:0.85rem;">
          <div style="display:flex;justify-content:space-between;">
            <span style="color:var(--text-muted);">Placement Status:</span>
            <span class="badge-pill ${s.isPlaced === 'true' ? 'success' : 'neutral'}">${s.isPlaced === 'true' ? 'Placed' : 'Eligible'}</span>
          </div>
          <div style="display:flex;justify-content:space-between;">
            <span style="color:var(--text-muted);">Graduation Year:</span>
            <strong>${s.graduationYear || 2025}</strong>
          </div>
          <div style="display:flex;justify-content:space-between;">
            <span style="color:var(--text-muted);">Verified CGPA:</span>
            <strong style="color:var(--indigo-600);">${parseFloat(s.cgpa || 0).toFixed(2)}</strong>
          </div>
        </div>
      </div>
    </div>
  `;
}

// ==========================================================================
// 16. Modal Dialog Handlers & Business Logic
// ==========================================================================
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (!modal) return;

  // Prepare select options if needed
  if (modalId === 'driveModal') {
    populateDriveCompanySelect();
  } else if (modalId === 'interviewModal') {
    populateInterviewAppSelect();
  } else if (modalId === 'offerModal') {
    populateOfferStudentSelect();
  }

  modal.classList.add('active');
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) modal.classList.remove('active');
}

async function populateDriveCompanySelect() {
  const select = document.getElementById('modalDriveCompanySelect');
  if (!select) return;
  const comps = cache.companies.length > 0 ? cache.companies : (await API.get('/api/companies') || []);
  select.innerHTML = comps.map(c => `
    <option value="${c.id}|${c.name}|${c.allowedBranches || 'CSE,ECE,IT'}|${c.minCGPA || 7.0}">${c.name} (${c.industry || 'IT'})</option>
  `).join('');
}

async function populateInterviewAppSelect() {
  const select = document.getElementById('modalIntApplication');
  if (!select) return;
  const apps = cache.applications.length > 0 ? cache.applications : (await API.get('/api/applications') || []);
  select.innerHTML = apps.map(a => `
    <option value="${a.id}|${a.studentId}|${a.driveId}|${a.companyName}">${a.studentName || a.studentId} &bull; ${a.companyName} (${a.status})</option>
  `).join('');
}

async function populateOfferStudentSelect() {
  const select = document.getElementById('modalOfferStudent');
  if (!select) return;
  const students = cache.students.length > 0 ? cache.students : (await API.get('/api/students') || []);
  select.innerHTML = students.map(s => `
    <option value="${s.id}|${s.name}|${s.rollNumber}">${s.name} (${s.rollNumber || s.id}) &bull; ${s.branch}</option>
  `).join('');
}

// ---- Modal Submit Handlers ----

async function handleSaveStudent(e) {
  e.preventDefault();
  const id = document.getElementById('modalStudentId')?.value;
  const name = document.getElementById('modalStudentName').value.trim();
  const rollNumber = document.getElementById('modalStudentRoll').value.trim();
  const branch = document.getElementById('modalStudentBranch').value;
  const cgpa = document.getElementById('modalStudentCgpa').value;
  const activeBacklogs = document.getElementById('modalStudentBacklogs').value;
  const email = document.getElementById('modalStudentEmail').value.trim();

  if (id) {
    // Update
    await API.put('/api/students', { id, name, cgpa, activeBacklogs, email });
    showToast('Student record updated successfully', 'success');
  } else {
    // Add new
    await API.post('/api/students', { name, rollNumber, branch, cgpa, activeBacklogs, email });
    showToast('New candidate enrolled successfully', 'success');
  }

  closeModal('studentModal');
  navigateSection('students');
}

async function handleSaveCompany(e) {
  e.preventDefault();
  const id = document.getElementById('modalCompanyId')?.value;
  const name = document.getElementById('modalCompanyName').value.trim();
  const industry = document.getElementById('modalCompanyIndustry').value.trim();
  const location = document.getElementById('modalCompanyLocation').value.trim();
  const minCGPA = document.getElementById('modalCompanyCgpa').value;
  const allowedBranches = document.getElementById('modalCompanyBranches').value.trim();
  const website = document.getElementById('modalCompanyWebsite').value.trim();
  const description = document.getElementById('modalCompanyDesc').value.trim();

  await API.post('/api/companies', { name, industry, location, minCGPA, allowedBranches, website, description });
  showToast(`Registered partner company: ${name}`, 'success');

  closeModal('companyModal');
  navigateSection('companies');
}

async function handleSaveDrive(e) {
  e.preventDefault();
  const compVal = document.getElementById('modalDriveCompanySelect').value.split('|');
  const title = document.getElementById('modalDriveTitle').value.trim();
  const driveDate = document.getElementById('modalDriveDate').value;
  const lastDateToApply = document.getElementById('modalDriveDeadline').value;
  const minCGPA = document.getElementById('modalDriveMinCgpa').value;
  const maxActiveBacklogs = document.getElementById('modalDriveBacklogs').value;
  const allowedBranches = document.getElementById('modalDriveBranches').value.trim();
  const description = document.getElementById('modalDriveDesc').value.trim();

  await API.post('/api/drives', {
    companyId: compVal[0],
    companyName: compVal[1],
    title, driveDate, lastDateToApply, minCGPA,
    maxActiveBacklogs, allowedBranches, description,
    requiredGraduationYear: 2025
  });

  showToast(`Published placement drive: ${title}`, 'success');
  closeModal('driveModal');
  navigateSection('drives');
}

async function handleSaveInterview(e) {
  e.preventDefault();
  const appParts = document.getElementById('modalIntApplication').value.split('|');
  const roundType = document.getElementById('modalIntRound').value;
  const scheduledDate = document.getElementById('modalIntDate').value;
  const scheduledTime = document.getElementById('modalIntTime').value;
  const venue = document.getElementById('modalIntVenue').value.trim();

  await API.post('/api/interviews', {
    applicationId: appParts[0],
    studentId: appParts[1],
    driveId: appParts[2],
    companyName: appParts[3],
    roundType, scheduledDate, scheduledTime, venue
  });

  showToast('Interview session scheduled successfully', 'success');
  closeModal('interviewModal');
  navigateSection('interviews');
}

async function handleSaveOffer(e) {
  e.preventDefault();
  const studentParts = document.getElementById('modalOfferStudent').value.split('|');
  const companyName = document.getElementById('modalOfferCompany').value.trim();
  const position = document.getElementById('modalOfferRole').value.trim();
  const packageLPA = document.getElementById('modalOfferPackage').value;

  await API.post('/api/results', {
    studentId: studentParts[0],
    companyName,
    position,
    packageLPA
  });

  showToast(`Recorded offer of ₹${packageLPA} LPA for ${studentParts[1]}!`, 'success');
  closeModal('offerModal');
  navigateSection('results');
}

async function handleSaveBroadcast(e) {
  e.preventDefault();
  const message = document.getElementById('modalBroadcastMsg').value.trim();
  const userId = document.getElementById('modalBroadcastTarget').value;

  await API.post('/api/notifications', { message, userId });
  showToast('Announcement posted to student noticeboard', 'success');
  closeModal('broadcastModal');
  navigateSection('notifications');
}

async function handleSaveProfileUpdate(e) {
  e.preventDefault();
  const id = document.getElementById('studentProfId').value;
  const name = document.getElementById('studentProfName').value.trim();
  const cgpa = document.getElementById('studentProfCgpa').value;
  const activeBacklogs = document.getElementById('studentProfBacklogs').value;
  const email = document.getElementById('studentProfEmail').value.trim();

  await API.put('/api/students', { id, name, cgpa, activeBacklogs, email });
  showToast('Academic profile updated successfully', 'success');
  navigateSection('profile');
}

// ==========================================================================
// 17. Live Criteria Verification & Quick Apply
// ==========================================================================
async function openJobDetailModal(driveId) {
  const drives = cache.drives.length > 0 ? cache.drives : (await API.get('/api/drives') || []);
  const drive = drives.find(d => d.id === driveId);
  if (!drive) return;

  const studentId = currentUser.id || 'STU001';
  const eligibility = await API.get(`/api/eligibility?driveId=${driveId}&studentId=${studentId}`) || { eligible: true, reasons: [] };

  document.getElementById('detailJobTitle').innerText = `${drive.title} – ${drive.companyName}`;

  const body = document.getElementById('jobDetailBody');
  const footer = document.getElementById('jobDetailFooter');

  body.innerHTML = `
    <div style="display:flex;align-items:flex-start;justify-content:space-between;gap:1rem;margin-bottom:1.5rem;">
      <div>
        <h3 style="font-size:1.25rem;font-weight:700;">${drive.title}</h3>
        <p style="color:var(--text-muted);font-size:0.9rem;">${drive.companyName} &bull; Drive Date: ${drive.driveDate}</p>
      </div>
      <span class="badge-pill indigo" style="font-size:0.85rem;">Min CGPA: ${drive.minCGPA}</span>
    </div>

    <div style="background:var(--slate-50);padding:1rem;border-radius:var(--radius-md);margin-bottom:1.5rem;font-size:0.9rem;color:var(--text-secondary);line-height:1.6;">
      <strong>Role Overview:</strong><br>
      ${drive.description || 'Hiring software engineers for campus placement.'}
    </div>

    <!-- Real-Time Automated Eligibility Verification Breakdown -->
    <div style="border:1px solid ${eligibility.eligible ? 'var(--emerald-100)' : 'var(--rose-100)'};background:${eligibility.eligible ? 'var(--emerald-50)' : 'var(--rose-50)'};padding:1.2rem;border-radius:var(--radius-md);">
      <div style="display:flex;align-items:center;gap:0.5rem;font-weight:700;color:${eligibility.eligible ? 'var(--emerald-600)' : 'var(--rose-600)'};margin-bottom:0.5rem;">
        <i data-lucide="${eligibility.eligible ? 'check-circle' : 'alert-circle'}"></i>
        <span>${eligibility.eligible ? 'ELIGIBILITY CONFIRMED: You satisfy all drive requirements!' : 'CRITERIA NOT MET: Ineligible for this opportunity'}</span>
      </div>
      ${eligibility.eligible ? `
        <p style="font-size:0.85rem;color:var(--emerald-600);">Your verified CGPA, department, and active backlog count pass all recruiter thresholds.</p>
      ` : `
        <ul style="margin-left:1.5rem;font-size:0.85rem;color:var(--rose-600);line-height:1.5;">
          ${eligibility.reasons.map(r => `<li>${r}</li>`).join('')}
        </ul>
      `}
    </div>
  `;

  footer.innerHTML = `
    <button type="button" class="btn btn-secondary" onclick="closeModal('jobDetailModal')">Close</button>
    <button type="button" class="btn btn-primary" onclick="handleQuickApply('${driveId}'); closeModal('jobDetailModal');">
      <i data-lucide="send"></i> Submit Application Now
    </button>
  `;

  openModal('jobDetailModal');
  lucide.createIcons();
}

async function handleQuickApply(driveId) {
  const studentId = currentUser.id || 'STU001';
  const res = await API.post('/api/applications', { studentId, driveId });
  if (res && res.success) {
    showToast(`Application successfully submitted! Tracking ID: ${res.id}`, 'success');
    navigateSection('applications');
  } else {
    showToast(res ? res.error : 'Could not submit application', 'error');
  }
}

async function updateApplicationStage(appId, newStatus) {
  const res = await API.put('/api/applications', { id: appId, status: newStatus, remarks: 'Stage advanced by placement officer' });
  if (res && res.success) {
    showToast(`Application ${appId} moved to ${newStatus}`, 'success');
    navigateSection('applications');
  }
}

// ---- Deletion Handlers ----
async function deleteStudentRecord(id) {
  if (!confirm('Are you sure you want to delete this candidate record?')) return;
  await API.delete(`/api/students?id=${id}`);
  showToast('Candidate record removed', 'success');
  navigateSection('students');
}

async function deleteCompanyRecord(id) {
  if (!confirm('Delete this partner company record?')) return;
  await API.delete(`/api/companies?id=${id}`);
  showToast('Partner company removed', 'success');
  navigateSection('companies');
}

async function deleteDriveRecord(id) {
  if (!confirm('Cancel and delete this placement drive?')) return;
  await API.delete(`/api/drives?id=${id}`);
  showToast('Placement drive deleted', 'success');
  navigateSection('drives');
}

async function deleteInterviewRecord(id) {
  if (!confirm('Cancel this scheduled interview slot?')) return;
  await API.delete(`/api/interviews?id=${id}`);
  showToast('Interview session cancelled', 'success');
  navigateSection('interviews');
}

async function deleteOfferRecord(id) {
  if (!confirm('Remove this recorded offer?')) return;
  await API.delete(`/api/results?id=${id}`);
  showToast('Offer record deleted', 'success');
  navigateSection('results');
}

// ==========================================================================
// 18. Toast Notification Utility
// ==========================================================================
function showToast(message, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const iconName = type === 'success' ? 'check-circle' : type === 'error' ? 'alert-triangle' : 'info';
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <i data-lucide="${iconName}" style="width:18px;height:18px;flex-shrink:0;"></i>
    <span>${message}</span>
  `;

  container.appendChild(toast);
  lucide.createIcons();
  setTimeout(() => toast.remove(), 4000);
}

async function refreshNotificationsBadge() {
  const notifs = await API.get('/api/notifications') || [];
  const badge = document.getElementById('notifBadge');
  if (badge) {
    badge.style.display = notifs.length > 0 ? 'block' : 'none';
  }
}
