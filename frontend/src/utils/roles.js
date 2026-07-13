export const ROLES = {
  STUDENT: 'STUDENT',
  RECRUITER: 'RECRUITER',
  ADMIN: 'ADMIN',
};

export const DASHBOARD_PATH_BY_ROLE = {
  [ROLES.STUDENT]: '/student',
  [ROLES.RECRUITER]: '/recruiter',
  [ROLES.ADMIN]: '/admin',
};

/**
 * The backend returns a user's roles as a set, e.g. ["STUDENT"]. A user is
 * expected to hold exactly one of the three roles in this app, so callers
 * treat the first entry as the primary role.
 */
export function getPrimaryRole(user) {
  if (!user || !Array.isArray(user.roles) || user.roles.length === 0) {
    return null;
  }
  return user.roles[0];
}

export function getDashboardPathForUser(user) {
  const role = getPrimaryRole(user);
  return DASHBOARD_PATH_BY_ROLE[role] ?? '/login';
}

export function hasAnyRole(user, allowedRoles) {
  const role = getPrimaryRole(user);
  return role != null && allowedRoles.includes(role);
}
