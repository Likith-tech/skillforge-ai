// Mocked until GET /admin/users exists on the backend. The shape mirrors
// UserProfileResponse so swapping this for `api.get('/admin/users')` later
// is a one-line change in fetchUsers.
const MOCK_USERS = [
  { id: 1, fullName: 'Asha Rao', email: 'asha.rao@example.com', roles: ['STUDENT'], enabled: true },
  { id: 2, fullName: 'Neel Kapoor', email: 'neel.kapoor@example.com', roles: ['RECRUITER'], enabled: true },
  { id: 3, fullName: 'Priya Sharma', email: 'priya.sharma@example.com', roles: ['STUDENT'], enabled: true },
  { id: 4, fullName: 'Admin User', email: 'admin@skillforge.ai', roles: ['ADMIN'], enabled: true },
];

export function fetchUsers() {
  return new Promise((resolve) => {
    setTimeout(() => resolve(MOCK_USERS), 400);
  });
}
