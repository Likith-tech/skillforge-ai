import Button from './Button';

/** Shared pager for every paginated list (jobs, applications, resume/ATS history). */
export default function Pagination({ page, totalPages, totalElements, onPageChange }) {
  if (totalPages <= 1) return null;

  const isFirst = page <= 0;
  const isLast = page >= totalPages - 1;

  return (
    <div className="flex items-center justify-between border-t border-slate-100 pt-3 text-sm text-slate-500">
      <p>
        Page {page + 1} of {totalPages}
        {typeof totalElements === 'number' && <> &middot; {totalElements} total</>}
      </p>
      <div className="flex gap-2">
        <Button
          type="button"
          variant="secondary"
          disabled={isFirst}
          onClick={() => onPageChange(page - 1)}
        >
          Previous
        </Button>
        <Button
          type="button"
          variant="secondary"
          disabled={isLast}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
