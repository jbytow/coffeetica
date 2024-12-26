import { Routes, Route } from 'react-router-dom';
import HomePage from './layouts/HomePage/HomePage';
import './App.css';
import Footer from './layouts/NavbarAndFooter/Footer';
import Navbar from './layouts/NavbarAndFooter/Navbar';
import AdminPage from './layouts/AdminPage/AdminPage';
import ManageCoffees from './layouts/AdminPage/components/ManageCoffees';
import ManageRoasteries from './layouts/AdminPage/components/ManageRoasteries';
import { CoffeePage } from './layouts/CoffeePage/CoffePage';

function App() {
  return (
    <>
      <Navbar />
      <div className="container my-4">
        <Routes>
          {/* Home Page */}
          <Route path="/" element={<HomePage />} />

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
    </>
  );
}

export default App;