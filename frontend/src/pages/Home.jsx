import { Link } from "react-router-dom";

function Home() {
  return (
    <main>
      <h1>SkillForge AI</h1>
      <p>Welcome to SkillForge AI.</p>
      <p>
        <Link to="/student/profile">Go to Student Profile</Link>
      </p>
    </main>
  );
}

export default Home;
