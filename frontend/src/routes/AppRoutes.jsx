import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home.jsx";
import ForgotPassword from "../pages/auth/ForgotPassword.jsx";
import Login from "../pages/auth/Login.jsx";
import Register from "../pages/auth/Register.jsx";
import ResetPassword from "../pages/auth/ResetPassword.jsx";
import ResumeAnalysisAnalytics from "../pages/resume/analysis/ResumeAnalytics.jsx";
import ResumeAnalysisDashboard from "../pages/resume/analysis/ResumeAnalysisDashboard.jsx";
import ResumeAnalysisHistory from "../pages/resume/analysis/ResumeAnalysisHistory.jsx";
import ResumeATSReport from "../pages/resume/analysis/ResumeATSReport.jsx";
import ResumeAnalysisComparison from "../pages/resume/analysis/ResumeComparison.jsx";
import ResumeKeywordAnalysis from "../pages/resume/analysis/ResumeKeywordAnalysis.jsx";
import ResumeSuggestions from "../pages/resume/analysis/ResumeSuggestions.jsx";
import ResumeDashboard from "../pages/resume/ResumeDashboard.jsx";
import ResumeHistory from "../pages/resume/ResumeHistory.jsx";
import ResumePreview from "../pages/resume/ResumePreview.jsx";
import ResumeUpload from "../pages/resume/ResumeUpload.jsx";
import JobBoard from "../pages/jobs/JobBoard.jsx";
import JobDetail from "../pages/jobs/JobDetail.jsx";
import StudentJobs from "../pages/jobs/StudentJobs.jsx";
import RecruiterJobs from "../pages/jobs/RecruiterJobs.jsx";
import RecruiterWorkflow from "../pages/recruiter/RecruiterWorkflow.jsx";
import StudentPlacementPortal from "../pages/placement/StudentPlacementPortal.jsx";
import EnterpriseAdminDashboard from "../pages/enterprise/EnterpriseAdminDashboard.jsx";
import EnterpriseNotifications from "../pages/enterprise/EnterpriseNotifications.jsx";
import EnterpriseFiles from "../pages/enterprise/EnterpriseFiles.jsx";
import EnterpriseReports from "../pages/enterprise/EnterpriseReports.jsx";
import StudentProfile from "../pages/student/StudentProfile.jsx";
import UserSettings from "../pages/user/UserSettings.jsx";
import ProtectedRoute from "./ProtectedRoute.jsx";

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route
        path="/student/profile"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <StudentProfile />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeAnalysisDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/ats"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeATSReport />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/keywords"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeKeywordAnalysis />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/suggestions"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeSuggestions />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/analytics"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeAnalysisAnalytics />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/history"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeAnalysisHistory />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/analysis/compare"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeAnalysisComparison />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/upload"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeUpload />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/preview"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumePreview />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/resume/history"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <ResumeHistory />
          </ProtectedRoute>
        }
      />
      <Route
        path="/jobs"
        element={
          <ProtectedRoute roles={["STUDENT", "RECRUITER", "ADMIN"]}>
            <JobBoard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/jobs/:jobId"
        element={
          <ProtectedRoute roles={["STUDENT", "RECRUITER", "ADMIN"]}>
            <JobDetail />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/jobs"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <StudentJobs />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/placement"
        element={
          <ProtectedRoute roles={["STUDENT"]}>
            <StudentPlacementPortal />
          </ProtectedRoute>
        }
      />
      <Route
        path="/recruiter/jobs"
        element={
          <ProtectedRoute roles={["RECRUITER"]}>
            <RecruiterJobs />
          </ProtectedRoute>
        }
      />
      <Route
        path="/recruiter/workflow"
        element={
          <ProtectedRoute roles={["RECRUITER"]}>
            <RecruiterWorkflow />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute roles={["ADMIN"]}>
            <EnterpriseAdminDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/notifications"
        element={
          <ProtectedRoute roles={["ADMIN", "RECRUITER", "STUDENT"]}>
            <EnterpriseNotifications />
          </ProtectedRoute>
        }
      />
      <Route
        path="/files"
        element={
          <ProtectedRoute roles={["STUDENT", "RECRUITER", "ADMIN"]}>
            <EnterpriseFiles />
          </ProtectedRoute>
        }
      />
      <Route
        path="/reports"
        element={
          <ProtectedRoute roles={["STUDENT", "RECRUITER", "ADMIN"]}>
            <EnterpriseReports />
          </ProtectedRoute>
        }
      />
      <Route
        path="/user/settings"
        element={
          <ProtectedRoute roles={["STUDENT", "RECRUITER", "ADMIN"]}>
            <UserSettings />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default AppRoutes;
