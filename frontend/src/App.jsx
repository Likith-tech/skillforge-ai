import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext.jsx";
import AppRoutes from "./routes/AppRoutes.jsx";

function App() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="app">
      <nav>
        <Link to="/">Home</Link>
        {" | "}
        <Link to="/student/profile">Student Profile</Link>
        {" | "}
        {isAuthenticated ? (
          <>
            <span>{user?.name}</span>
            {" "}
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
