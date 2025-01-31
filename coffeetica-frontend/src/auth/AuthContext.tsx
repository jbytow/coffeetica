import React, { createContext, useState, useEffect} from "react";
import apiClient from "../lib/api";
import { UserDTO } from "../models/UserDTO";

  interface AuthContextValue {
    isAuthenticated: boolean;
    user: UserDTO | null;
    login: (token: string) => Promise<void>;
    logout: () => void;
  }
  
  interface AuthProviderProps {
    children: React.ReactNode;
  }
  
  // create context
  export const AuthContext = createContext<AuthContextValue>({
    isAuthenticated: false,
    user: null,
    login: async () => { /* no-op */ },
    logout: () => { /* no-op */ },
  });
  
  // Provider holds the logic
  export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState<UserDTO | null>(null);
  
    useEffect(() => {
      const token = localStorage.getItem('token');
      if (token) {
        apiClient.get<UserDTO>('/users/me')
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
      localStorage.setItem('token', token);
      try {
        const res = await apiClient.get<UserDTO>('/users/me');
        setUser(res.data);
        setIsAuthenticated(true);
      } catch {
        logout();
      }
    };
  
    const logout = () => {
      localStorage.removeItem('token');
      setUser(null);
      setIsAuthenticated(false);
    };
  
    const value: AuthContextValue = {
      isAuthenticated,
      user,
      login,
      logout,
    };
  
    return (
      <AuthContext.Provider value={value}>
        {children}
      </AuthContext.Provider>
    );
  };