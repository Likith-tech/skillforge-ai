import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext.jsx";
import AppRoutes from "./routes/AppRoutes.jsx";

function App() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <div className="app">
      <nav>
        <Link to="/">Home</Link>
        {" | "}
        {user?.role === "STUDENT" && (
          <>
            <Link to="/student/profile">Student Profile</Link>
            {" | "}
            <Link to="/student/resume">Resume</Link>
            {" | "}
            <Link to="/student/resume/analysis">Analysis</Link>
            {" | "}
            <Link to="/student/jobs">Career Radar</Link>
            {" | "}
            <Link to="/student/placement">Placement</Link>
            {" | "}
          </>
        )}
        {user?.role === "RECRUITER" && (
          <>
            <Link to="/recruiter/jobs">Recruiter Jobs</Link>
            {" | "}
            <Link to="/recruiter/workflow">Workflow</Link>
            {" | "}
          </>
        )}
        {user?.role === "ADMIN" && (
          <>
            <Link to="/admin/dashboard">Admin Dashboard</Link>
            {" | "}
            <Link to="/reports">Reports</Link>
            {" | "}
          </>
        )}
        {isAuthenticated && (
          <>
            <Link to="/jobs">Job Board</Link>
            {" | "}
            <Link to="/admin/notifications">Notifications</Link>
            {" | "}
            <Link to="/files">Files</Link>
            {" | "}
            <Link to="/reports">Reports</Link>
            {" | "}
          </>
        )}
        {isAuthenticated ? (
          <>
            <span>{user?.name}</span>
            {" "}
            <Link to="/user/settings">Settings</Link>
            {" | "}
            <button type="button" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            {" | "}
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
      <AppRoutes />
    </div>
  );
}

export default App;
