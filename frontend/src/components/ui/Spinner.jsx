export default function Spinner({ className = 'h-8 w-8' }) {
  return (
    <div
      role="status"
      aria-label="Loading"
      className={`animate-spin rounded-full border-4 border-slate-200 border-t-indigo-600 ${className}`}
    />
  );
}
