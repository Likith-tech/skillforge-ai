import { Link } from "react-router-dom";

function Home() {
  return (
    <main>
      <h1>SkillForge AI</h1>
      <p>Resume intelligence, job matching, and placement readiness in one workspace.</p>
      <p>
        <Link to="/student/profile">Go to Student Profile</Link>
      </p>
      <p>
        <Link to="/student/resume">Go to Resume</Link>
      </p>
      <p>
        <Link to="/jobs">Go to Job Board</Link>
      </p>
      <p>
        <Link to="/admin/dashboard">Go to Admin Dashboard</Link>
      </p>
    </main>
  );
}

export default Home;
