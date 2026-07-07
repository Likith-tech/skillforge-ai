import { useEffect, useState } from "react";
import {
  createStudentProfile,
  getStudentProfile,
  updateStudentProfile,
} from "../../services/studentProfileService";
import "./StudentProfile.css";

const initialForm = {
  fullName: "",
  collegeName: "",
  degree: "",
  branch: "",
  graduationYear: "",
  skills: "",
  experience: "",
  projects: "",
  certifications: "",
  githubUrl: "",
  linkedinUrl: "",
};

const fields = [
  { name: "fullName", label: "Full Name", required: true },
  { name: "collegeName", label: "College Name" },
  { name: "degree", label: "Degree" },
  { name: "branch", label: "Branch" },
  { name: "graduationYear", label: "Graduation Year", type: "number" },
  { name: "githubUrl", label: "GitHub URL", type: "url" },
  { name: "linkedinUrl", label: "LinkedIn URL", type: "url" },
];

const textAreas = [
  { name: "skills", label: "Skills" },
  { name: "experience", label: "Experience" },
  { name: "projects", label: "Projects" },
  { name: "certifications", label: "Certifications" },
];

function StudentProfile() {
  const [form, setForm] = useState(initialForm);
  const [profileExists, setProfileExists] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profile = await getStudentProfile();
        setForm({
          fullName: profile.fullName || "",
          collegeName: profile.collegeName || "",
          degree: profile.degree || "",
          branch: profile.branch || "",
          graduationYear: profile.graduationYear || "",
          skills: profile.skills || "",
          experience: profile.experience || "",
          projects: profile.projects || "",
          certifications: profile.certifications || "",
          githubUrl: profile.githubUrl || "",
          linkedinUrl: profile.linkedinUrl || "",
        });
        setProfileExists(true);
      } catch (loadError) {
        if (loadError.response?.status !== 404) {
          setError("Unable to load profile. Please try again.");
        }
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setMessage("");
    setError("");

    const payload = {
      ...form,
      graduationYear: form.graduationYear ? Number(form.graduationYear) : null,
    };

    try {
      const savedProfile = profileExists
        ? await updateStudentProfile(payload)
        : await createStudentProfile(payload);

      setForm({
        fullName: savedProfile.fullName || "",
        collegeName: savedProfile.collegeName || "",
        degree: savedProfile.degree || "",
        branch: savedProfile.branch || "",
        graduationYear: savedProfile.graduationYear || "",
        skills: savedProfile.skills || "",
        experience: savedProfile.experience || "",
        projects: savedProfile.projects || "",
        certifications: savedProfile.certifications || "",
        githubUrl: savedProfile.githubUrl || "",
        linkedinUrl: savedProfile.linkedinUrl || "",
      });
      setProfileExists(true);
      setMessage("Profile saved successfully.");
    } catch (saveError) {
      setError(saveError.response?.data?.message || "Unable to save profile.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="student-profile-page">Loading profile...</div>;
  }

  return (
    <main className="student-profile-page">
      <section className="student-profile-header">
        <h1>Student Profile</h1>
        <p>Manage your professional details for SkillForge AI.</p>
      </section>

      <form className="student-profile-form" onSubmit={handleSubmit}>
        {error && <div className="form-alert form-alert-error">{error}</div>}
        {message && <div className="form-alert form-alert-success">{message}</div>}

        <div className="profile-form-grid">
          {fields.map((field) => (
            <label className="profile-field" key={field.name}>
              <span>{field.label}</span>
              <input
                name={field.name}
                type={field.type || "text"}
                value={form[field.name]}
                onChange={handleChange}
                required={field.required}
              />
            </label>
          ))}
        </div>

        {textAreas.map((field) => (
          <label className="profile-field" key={field.name}>
            <span>{field.label}</span>
            <textarea
              name={field.name}
              rows="4"
              value={form[field.name]}
              onChange={handleChange}
            />
          </label>
        ))}

        <button type="submit" disabled={saving}>
          {saving ? "Saving..." : profileExists ? "Update Profile" : "Create Profile"}
        </button>
      </form>
    </main>
  );
}

export default StudentProfile;
