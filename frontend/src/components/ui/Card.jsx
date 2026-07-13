export default function Card({ title, action, className = '', children }) {
  return (
    <section className={`rounded-xl bg-white p-6 shadow-sm ring-1 ring-slate-200 ${className}`}>
      {(title || action) && (
        <div className="mb-4 flex items-center justify-between">
          {title && <h2 className="text-base font-semibold text-slate-900">{title}</h2>}
          {action}
        </div>
      )}
      {children}
    </section>
  );
}
