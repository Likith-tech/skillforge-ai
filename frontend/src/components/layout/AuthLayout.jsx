export default function AuthLayout({ title, subtitle, children }) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-12">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            SkillForge <span className="text-indigo-600">AI</span>
          </h1>
          <p className="mt-1 text-sm text-slate-500">Career &amp; Placement Intelligence Platform</p>
        </div>
        <div className="rounded-xl bg-white p-8 shadow-sm ring-1 ring-slate-200">
          <h2 className="text-xl font-semibold text-slate-900">{title}</h2>
          {subtitle && <p className="mt-1 text-sm text-slate-500">{subtitle}</p>}
          <div className="mt-6">{children}</div>
        </div>
      </div>
    </div>
  );
}
