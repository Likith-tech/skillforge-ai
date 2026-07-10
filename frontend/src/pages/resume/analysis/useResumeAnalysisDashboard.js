import { useEffect, useState } from "react";
import { getResumeAnalysisDashboard } from "../../../services/resumeAnalysisService";

export function useResumeAnalysisDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    const load = async () => {
      try {
        const dashboard = await getResumeAnalysisDashboard();
        if (active) {
          setData(dashboard);
        }
      } catch (loadError) {
        if (active) {
          setError(loadError.response?.data?.message || "Unable to load resume analysis.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    load();

    return () => {
      active = false;
    };
  }, []);

  return { data, loading, error, setData, setError };
}