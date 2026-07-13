import ProfileCard from '../../components/student/ProfileCard';
import ResumeUpload from '../../components/student/ResumeUpload';
import AtsScoreCard from '../../components/student/AtsScoreCard';
import RecommendedJobsWidget from '../../components/student/RecommendedJobsWidget';
import { useCurrentResume } from '../../hooks/useCurrentResume';

export default function StudentDashboard() {
  const { resume, loading, refresh } = useCurrentResume();

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900">Welcome back</h2>
        <p className="text-sm text-slate-500">Here&apos;s where your placement journey stands today.</p>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-1">
          <ProfileCard />
          <AtsScoreCard resume={resume} loading={loading} />
          <ResumeUpload hasExistingResume={Boolean(resume)} onUploaded={refresh} />
        </div>
        <div className="lg:col-span-2">
          <RecommendedJobsWidget />
        </div>
      </div>
    </div>
  );
}
