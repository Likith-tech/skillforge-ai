const SIZE_CLASSES = {
  md: 'h-20 w-20 text-lg',
  lg: 'h-32 w-32 text-3xl',
};

export default function CircularScore({ score, size = 'md' }) {
  const dimension = SIZE_CLASSES[size] ?? SIZE_CLASSES.md;

  return (
    <div className={`relative shrink-0 ${dimension}`}>
      <svg viewBox="0 0 36 36" className={`-rotate-90 ${dimension}`}>
        <path
          className="stroke-slate-200"
          fill="none"
          strokeWidth="3.5"
          d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
        />
        <path
          className="stroke-indigo-600"
          fill="none"
          strokeWidth="3.5"
          strokeLinecap="round"
          strokeDasharray={`${score}, 100`}
          d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
        />
      </svg>
      <span className="absolute inset-0 flex items-center justify-center font-bold text-slate-900">{score}</span>
    </div>
  );
}
