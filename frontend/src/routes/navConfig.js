import { ROLES } from '../utils/roles';

export const NAV_ITEMS_BY_ROLE = {
  [ROLES.STUDENT]: [
    { to: '/student', label: 'Dashboard', end: true },
    { to: '/student/jobs', label: 'Jobs' },
    { to: '/student/jobs/recommended', label: 'Recommended' },
    { to: '/student/jobs/saved', label: 'Saved Jobs' },
    { to: '/student/applications', label: 'Applications' },
    { to: '/student/resume', label: 'Resume' },
    { to: '/student/ats-report', label: 'ATS Report' },
  ],
  [ROLES.RECRUITER]: [
    { to: '/recruiter', label: 'Dashboard', end: true },
    { to: '/recruiter/jobs', label: 'My Jobs' },
    { to: '/recruiter/company', label: 'Company' },
  ],
  [ROLES.ADMIN]: [{ to: '/admin', label: 'Dashboard', end: true }],
};
