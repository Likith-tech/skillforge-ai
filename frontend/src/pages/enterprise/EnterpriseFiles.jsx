import { useEffect, useState } from "react";
import { deleteFile, getFiles, uploadCompanyLogo, uploadPhoto } from "../../services/enterpriseService";
import "./EnterprisePortal.css";

function EnterpriseFiles() {
  const [files, setFiles] = useState([]);
  const [photo, setPhoto] = useState(null);
  const [logo, setLogo] = useState(null);

  useEffect(() => {
    const load = async () => setFiles(await getFiles());
    load();
  }, []);

  const reload = async () => setFiles(await getFiles());

  const remove = async (fileId) => {
    await deleteFile(fileId);
    await reload();
  };

  return (
    <main className="enterprise-page">
      <section className="enterprise-shell">
        <header className="enterprise-header">
          <div>
            <h1>File Management</h1>
            <p>Profile photos, company logos, and uploaded assets.</p>
          </div>
        </header>
        <div className="enterprise-grid">
          <section className="enterprise-card enterprise-section">
            <div className="enterprise-form-grid">
              <label className="enterprise-field enterprise-full">
                <span>Profile Photo</span>
                <input type="file" className="enterprise-input" onChange={(event) => setPhoto(event.target.files?.[0] || null)} />
              </label>
              <button className="enterprise-button" type="button" onClick={async () => { if (photo) { await uploadPhoto(photo); await reload(); } }}>Upload Photo</button>
              <label className="enterprise-field enterprise-full">
                <span>Company Logo</span>
                <input type="file" className="enterprise-input" onChange={(event) => setLogo(event.target.files?.[0] || null)} />
              </label>
              <button className="enterprise-button secondary" type="button" onClick={async () => { if (logo) { await uploadCompanyLogo(logo); await reload(); } }}>Upload Logo</button>
            </div>
          </section>
          <aside className="enterprise-card enterprise-sidebar">
            <h3>Uploaded Assets</h3>
            <ul className="enterprise-list">
              {files.map((item) => (
                <li className="enterprise-list-item" key={item.id}>
                  <div className="enterprise-actions" style={{ justifyContent: "space-between" }}>
                    <div>{item.assetType} · {item.originalFileName}</div>
                    <button className="enterprise-button secondary" type="button" onClick={() => remove(item.id)}>Delete</button>
                  </div>
                </li>
              ))}
            </ul>
          </aside>
        </div>
      </section>
    </main>
  );
}

export default EnterpriseFiles;