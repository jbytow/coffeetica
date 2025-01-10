import { Routes, Route } from 'react-router-dom';
import HomePage from './layouts/HomePage/HomePage';
import './App.css';
import Footer from './layouts/NavbarAndFooter/Footer';
import Navbar from './layouts/NavbarAndFooter/Navbar';
import AdminPage from './layouts/AdminPage/AdminPage';
import ManageCoffees from './layouts/AdminPage/components/ManageCoffees';
import ManageRoasteries from './layouts/AdminPage/components/ManageRoasteries';
import { CoffeePage } from './layouts/CoffeePage/CoffeePage';
import RegisterPage from './auth/RegisterPage';
import LoginPage from './auth/LoginPage';

function App() {
  return (
    <div className='d-flex flex-column min-vh-100'>
      <Navbar />
      <div className='flex-grow-1'>
        <Routes>
          {/* Home Page */}
          <Route path="/" element={<HomePage />} />

          {/* Auth */}
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />

          {/* Admin Page */}
          <Route path="/admin" element={<AdminPage />} />

          {/* Nested Routes for Admin Sections */}
          <Route path="/admin/coffees" element={<ManageCoffees />} />
          <Route path="/admin/roasteries" element={<ManageRoasteries />} />

           {/* Coffee Details Page */}
           <Route path="/coffees/:id" element={<CoffeePage />} />

        </Routes>
      </div>
      <Footer />
    </div>
  );
}

export default App;