import React, { useContext, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import apiClient from '../../lib/api';
import { AuthContext } from '../../auth/AuthContext';

const LoginPage: React.FC = () => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      const response = await apiClient.post('/auth/login', { identifier, password });

      await login(response.data.token);
      setError(null);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.error || 'An unexpected error occurred. Please try again.');
    }
  };

  const handleTestLogin = async () => {
    try {
      const response = await apiClient.post('/auth/auto-login');
      await login(response.data.token);
      navigate('/');
    } catch (err: any) {
      setError('Test login failed. Please try again later.');
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-start pt-5 vh-100">
      <div className="card shadow-lg" style={{ maxWidth: '400px', width: '100%' }}>
        <div className="card-body p-4">
          <h2 className="card-title text-center mb-4">Login</h2>
          <form onSubmit={handleLogin}>
            <div className="mb-3">
              <label htmlFor="identifier" className="form-label">Username or E-mail</label>
              <input
                type="text"
                id="identifier"
                className="form-control"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
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
          <div className="mt-3 text-center">
            <button
              type="button"
              className="btn btn-outline-secondary w-100"
              onClick={handleTestLogin}
            >
              Login as Test User
            </button>
          </div>
          <div className="text-center mt-3">
            <p className="mb-0">Don't have an account? <Link to="/register">Register here</Link>.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;