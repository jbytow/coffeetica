import { Routes, Route } from 'react-router-dom';
import HomePage from './layouts/HomePage/HomePage';
import './App.css';
import Footer from './layouts/NavbarAndFooter/Footer';
import Navbar from './layouts/NavbarAndFooter/Navbar';
import AdminPage from './layouts/AdminPage/AdminPage';
import ManageCoffees from './layouts/AdminPage/components/ManageCoffees/ManageCoffees';
import ManageRoasteries from './layouts/AdminPage/components/ManageRoasteries/ManageRoasteries';
import { CoffeePage } from './layouts/CoffeePage/CoffeePage';
import RegisterPage from './auth/RegisterPage';
import LoginPage from './auth/LoginPage';
import CreateCoffee from './layouts/AdminPage/components/ManageCoffees/CreateCoffee';
import EditCoffee from './layouts/AdminPage/components/ManageCoffees/EditCoffee';
import CreateRoastery from './layouts/AdminPage/components/ManageRoasteries/CreateRoastery';
import EditRoastery from './layouts/AdminPage/components/ManageRoasteries/EditRoastery';
import CoffeesListPage from './layouts/CoffeesListPage/CoffeesListPage';
import RoasteriesListPage from './layouts/RoasteriesListPage/RoasteriesListPage';
import { AuthProvider } from './auth/AuthContext';
import { ReviewListPage } from './layouts/CoffeePage/components/ReviewListPage/ReviewListPage';
import { RoasteryPage } from './layouts/RoasteryPage/RoasteryPage';

function App() {
  return (
    <AuthProvider>
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
            <Route path="/admin/coffees/add" element={<CreateCoffee />} />
            <Route path="/admin/coffees/edit/:id" element={<EditCoffee />} />
            <Route path="/admin/roasteries" element={<ManageRoasteries />} />
            <Route path="/admin/roasteries/add" element={<CreateRoastery />} />
            <Route path="/admin/roasteries/edit/:id" element={<EditRoastery />} />

            {/* Roasteries and Coffees Pages */}
            <Route path="/roasteries" element={<RoasteriesListPage />} />
            <Route path="/roasteries/:id" element={<RoasteryPage />} />
            <Route path="/coffees" element={<CoffeesListPage />} />
            <Route path="/coffees/:id" element={<CoffeePage />} />
            <Route path="/coffees/:coffeeId/reviews" element={<ReviewListPage />} />

          </Routes>
        </div>
        <Footer />
      </div>
    </AuthProvider>
  );
}

export default App;