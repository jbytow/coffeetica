import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api', // eg. VITE_API_URL=https://api.coffeetica.eu for production and 'http://localhost:8080/api' for development
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to attach the JWT token to the Authorization header
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token'); // Retrieve token from localStorage
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // Add token to request headers
    }
    return config;
  },
  (error) => Promise.reject(error) // Handle request errors
);

export default apiClient;