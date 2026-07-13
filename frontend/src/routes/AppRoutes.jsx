import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import PublicOnlyRoute from './PublicOnlyRoute';
import DashboardLayout from '../components/layout/DashboardLayout';
import { NAV_ITEMS_BY_ROLE } from './navConfig';
import { ROLES } from '../utils/roles';

import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import NotFoundPage from '../pages/NotFoundPage';
import UnauthorizedPage from '../pages/UnauthorizedPage';
import StudentDashboard from '../pages/student/StudentDashboard';
import ResumePage from '../pages/student/ResumePage';
import AtsReportPage from '../pages/student/AtsReportPage';
import Jobs from '../pages/student/Jobs';
import JobDetails from '../pages/student/JobDetails';
import SavedJobs from '../pages/student/SavedJobs';
import RecommendedJobs from '../pages/student/RecommendedJobs';
import Applications from '../pages/student/Applications';
import RecruiterDashboard from '../pages/recruiter/RecruiterDashboard';
import CompanyProfile from '../pages/recruiter/CompanyProfile';
import CreateJob from '../pages/recruiter/CreateJob';
import ManageJobs from '../pages/recruiter/ManageJobs';
import Applicants from '../pages/recruiter/Applicants';
import AdminDashboard from '../pages/admin/AdminDashboard';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route element={<PublicOnlyRoute />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.STUDENT]} />}>
        <Route
          path="/student"
          element={<DashboardLayout navItems={NAV_ITEMS_BY_ROLE[ROLES.STUDENT]} title="Student dashboard" />}
        >
          <Route index element={<StudentDashboard />} />
          <Route path="resume" element={<ResumePage />} />
          <Route path="ats-report" element={<AtsReportPage />} />
          <Route path="jobs" element={<Jobs />} />
          <Route path="jobs/recommended" element={<RecommendedJobs />} />
          <Route path="jobs/saved" element={<SavedJobs />} />
          <Route path="jobs/:id" element={<JobDetails />} />
          <Route path="applications" element={<Applications />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.RECRUITER]} />}>
        <Route
          path="/recruiter"
          element={<DashboardLayout navItems={NAV_ITEMS_BY_ROLE[ROLES.RECRUITER]} title="Recruiter dashboard" />}
        >
          <Route index element={<RecruiterDashboard />} />
          <Route path="company" element={<CompanyProfile />} />
          <Route path="jobs" element={<ManageJobs />} />
          <Route path="jobs/new" element={<CreateJob />} />
          <Route path="jobs/:id/edit" element={<CreateJob />} />
          <Route path="jobs/:jobId/applicants" element={<Applicants />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.ADMIN]} />}>
        <Route
          path="/admin"
          element={<DashboardLayout navItems={NAV_ITEMS_BY_ROLE[ROLES.ADMIN]} title="Admin dashboard" />}
        >
          <Route index element={<AdminDashboard />} />
        </Route>
      </Route>

      <Route path="/unauthorized" element={<UnauthorizedPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
