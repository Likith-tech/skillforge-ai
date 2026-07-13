const STYLES = {
  error: 'bg-rose-50 text-rose-700 ring-1 ring-inset ring-rose-200',
  success: 'bg-emerald-50 text-emerald-700 ring-1 ring-inset ring-emerald-200',
  info: 'bg-indigo-50 text-indigo-700 ring-1 ring-inset ring-indigo-200',
};

export default function Alert({ variant = 'error', children }) {
  if (!children) return null;

  return (
    <div role="alert" className={`rounded-md px-4 py-3 text-sm ${STYLES[variant]}`}>
      {children}
    </div>
  );
}
