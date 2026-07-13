import api from './api';

export function registerUser({ fullName, email, password, role }) {
  return api.post('/auth/register', { fullName, email, password, role }).then((res) => res.data);
}

export function loginUser({ email, password }) {
  return api.post('/auth/login', { email, password }).then((res) => res.data);
}

export function fetchProfile() {
  return api.get('/auth/profile').then((res) => res.data);
}
