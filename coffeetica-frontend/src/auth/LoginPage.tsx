import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../lib/api';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null); // Store only the error message
  const navigate = useNavigate();

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      const response = await apiClient.post('/auth/login', {
        username,
        password,
      });

      // Save the JWT token in localStorage
      localStorage.setItem('token', response.data);

      // Save user ID and roles
      localStorage.setItem('userId', response.data.id);
      localStorage.setItem('roles', JSON.stringify(response.data.roles));

      // Clear the error state if login is successful
      setError(null);
      navigate('/dashboard'); // Redirect to the protected route
    } catch (err: any) {
      // Handle the error - extract the error message
      const errorMessage =
        err.response?.data?.error || 'An unexpected error occurred. Please try again.';
      setError(errorMessage);
    }
  };


  return (
    <div className="container d-flex justify-content-center align-items-start pt-5 vh-100">
      <div className="card shadow-lg" style={{ maxWidth: '400px', width: '100%' }}>
        <div className="card-body p-4">
          <h2 className="card-title text-center mb-4">Login</h2>
          <form onSubmit={handleLogin}>
            <div className="mb-3">
              <label htmlFor="username" className="form-label">Username</label>
              <input
                type="text"
                id="username"
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label htmlFor="password" className="form-label">Password</label>
              <input
                type="password"
                id="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            {error && <div className="alert alert-danger">{error}</div>}
            <button type="submit" className="btn btn-primary w-100">Login</button>
          </form>
          <div className="text-center mt-3">
            <p className="mb-0">Don't have an account? <Link to="/register">Register here</Link>.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;