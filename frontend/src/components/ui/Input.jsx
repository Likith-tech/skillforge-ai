export default function Input({ label, id, error, className = '', ...props }) {
  return (
    <div className={className}>
      {label && (
        <label htmlFor={id} className="mb-1 block text-sm font-medium text-slate-700">
          {label}
        </label>
      )}
      <input
        id={id}
        className={`block w-full rounded-md border-0 px-3 py-2 text-slate-900 shadow-sm ring-1 ring-inset placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm ${
          error ? 'ring-rose-400' : 'ring-slate-300'
        }`}
        {...props}
      />
      {error && <p className="mt-1 text-sm text-rose-600">{error}</p>}
    </div>
  );
}
