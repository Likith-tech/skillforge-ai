import { downloadReport } from "../../services/enterpriseService";
import { useState } from "react";
import "./EnterprisePortal.css";

function EnterpriseReports() {
  const [busy, setBusy] = useState(false);

  const download = async (path, fileName) => {
    setBusy(true);
    try {
      const blob = await downloadReport(path);
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = fileName;
      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();
      URL.revokeObjectURL(url);
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="enterprise-page">
      <section className="enterprise-shell">
        <header className="enterprise-header">
          <div>
            <h1>Reports</h1>
            <p>Generate CSV and spreadsheet reports from live placement data.</p>
          </div>
        </header>
        <div className="enterprise-grid">
          <section className="enterprise-card enterprise-section">
            <div className="enterprise-actions">
              <button className="enterprise-button" disabled={busy} onClick={() => download("/reports/admin/placement-report.csv", "placement-report.csv")} type="button">Placement CSV</button>
              <button className="enterprise-button secondary" disabled={busy} onClick={() => download("/reports/students/me/placement.csv", "student-placement.csv")} type="button">Student CSV</button>
              <button className="enterprise-button secondary" disabled={busy} onClick={() => download("/reports/recruiter/jobs/1/applicants.xlsx", "applicants.xlsx")} type="button">Applicants XLSX</button>
            </div>
          </section>
        </div>
      </section>
    </main>
  );
}

export default EnterpriseReports;