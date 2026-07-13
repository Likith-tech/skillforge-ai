import { useEffect, useState } from 'react';
import { loginUser, registerUser, fetchProfile } from '../services/authService';
import { getToken, setToken, clearToken } from '../utils/storage';
import { AuthContext } from './authContextInstance';

function mapAuthResponseToUser(authResponse) {
  return {
    id: authResponse.userId,
    fullName: authResponse.fullName,
    email: authResponse.email,
    roles: Array.from(authResponse.roles ?? []),
  };
}

function mapProfileToUser(profile) {
  return {
    id: profile.id,
    fullName: profile.fullName,
    email: profile.email,
    roles: Array.from(profile.roles ?? []),
  };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      setLoading(false);
      return;
    }

    fetchProfile()
      .then((profile) => setUser(mapProfileToUser(profile)))
      .catch(() => {
        clearToken();
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  async function login(credentials) {
    const authResponse = await loginUser(credentials);
    setToken(authResponse.token);
    const nextUser = mapAuthResponseToUser(authResponse);
    setUser(nextUser);
    return nextUser;
  }

  async function register(payload) {
    const authResponse = await registerUser(payload);
    setToken(authResponse.token);
    const nextUser = mapAuthResponseToUser(authResponse);
    setUser(nextUser);
    return nextUser;
  }

  function logout() {
    clearToken();
    setUser(null);
  }

  const value = {
    user,
    loading,
    isAuthenticated: user != null,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
