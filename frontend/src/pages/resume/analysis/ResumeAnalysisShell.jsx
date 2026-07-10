import ResumeAnalysisTabs from "./ResumeAnalysisTabs";

function ResumeAnalysisShell({ title, subtitle, actions, children }) {
  return (
    <main className="analysis-page">
      <section className="analysis-header">
        <div>
          <p className="analysis-eyebrow">AI Resume Analysis</p>
          <h1>{title}</h1>
          <p>{subtitle}</p>
        </div>
        {actions && <div className="analysis-actions">{actions}</div>}
      </section>

      <ResumeAnalysisTabs />

      <section className="analysis-content">{children}</section>
    </main>
  );
}

export default ResumeAnalysisShell;