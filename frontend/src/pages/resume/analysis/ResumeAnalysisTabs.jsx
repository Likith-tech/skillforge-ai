import { NavLink } from "react-router-dom";

const tabs = [
  { to: "/student/resume/analysis", label: "Dashboard" },
  { to: "/student/resume/analysis/ats", label: "ATS Report" },
  { to: "/student/resume/analysis/keywords", label: "Keywords" },
  { to: "/student/resume/analysis/suggestions", label: "Suggestions" },
  { to: "/student/resume/analysis/analytics", label: "Analytics" },
  { to: "/student/resume/analysis/history", label: "History" },
  { to: "/student/resume/analysis/compare", label: "Compare" },
];

function ResumeAnalysisTabs() {
  return (
    <nav className="analysis-tabs" aria-label="Resume analysis pages">
      {tabs.map((tab) => (
        <NavLink
          key={tab.to}
          to={tab.to}
          className={({ isActive }) => `analysis-tab ${isActive ? "active" : ""}`}
          end={tab.to === "/student/resume/analysis"}
        >
          {tab.label}
        </NavLink>
      ))}
    </nav>
  );
}

export default ResumeAnalysisTabs;