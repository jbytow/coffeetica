import React, { createContext, useState, useEffect, useCallback} from "react";
import apiClient from "../lib/api";
import { UserDTO } from "../models/UserDTO";

interface AuthContextValue {
  isAuthenticated: boolean;
  user: UserDTO | null;
  login: (token: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
  updateUser: (user: UserDTO) => void;
}

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthContext = createContext<AuthContextValue>({
  isAuthenticated: false,
  user: null,
  login: async () => { /* no-op */ },
  logout: () => { /* no-op */ },
  hasRole: () => false,
  updateUser: () => {},
});

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<UserDTO | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      apiClient.get<UserDTO>("/users/me")
        .then((res) => {
          setUser(res.data);
          setIsAuthenticated(true);
        })
        .catch(() => {
          logout();
        });
    }
  }, []);

  const login = async (token: string) => {
    localStorage.setItem("token", token);
    try {
      const res = await apiClient.get<UserDTO>("/users/me");
      setUser(res.data);
      setIsAuthenticated(true);
    } catch {
      logout();
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    setIsAuthenticated(false);
  };

  // Method for checking whether user has required role
  const hasRole = useCallback((role: string) => {
    return user?.roles?.includes(role) ?? false;
  }, [user]);

  const updateUser = (updatedUser: UserDTO) => {
    setUser(updatedUser);
  };

  const value: AuthContextValue = {
    isAuthenticated,
    user,
    login,
    logout,
    hasRole,
    updateUser,
  };
  
    return (
      <AuthContext.Provider value={value}>
        {children}
      </AuthContext.Provider>
    );
  };