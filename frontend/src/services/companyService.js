import api from './api';

export function getMyCompany() {
  return api.get('/companies/mine').then((res) => res.data);
}

export function getCompany(id) {
  return api.get(`/companies/${id}`).then((res) => res.data);
}

export function createCompany(payload) {
  return api.post('/companies', payload).then((res) => res.data);
}

export function updateCompany(id, payload) {
  return api.put(`/companies/${id}`, payload).then((res) => res.data);
}
