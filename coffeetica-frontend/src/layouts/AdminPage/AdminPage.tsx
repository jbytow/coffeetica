import React, { useContext, useState } from "react";
import ManageCoffees from "./components/ManageCoffees/ManageCoffees";
import ManageRoasteries from "./components/ManageRoasteries/ManageRoasteries";
import { AuthContext } from "../../auth/AuthContext";
import { Navigate } from "react-router-dom";
import ManageUsers from "./components/ManageUsers/ManageUsers";
// In the future, you can add more sections like ManageUsers
// import ManageUsers from "./ManageUsers";

const AdminPage: React.FC = () => {
  // State to keep track of the currently active section
  const {  hasRole } = useContext(AuthContext);
  const [activeSection, setActiveSection] = useState("coffees");

  if (!hasRole("Admin")) {
    return <Navigate to="/" replace />;
  }

  // Function to render the appropriate section based on the active state
  const renderActiveSection = () => {
    switch (activeSection) {
      case "coffees":
        return <ManageCoffees />;
      case "roasteries":
        return <ManageRoasteries />;
      case "users":
        return <ManageUsers />;
      default:
        return <p>Select a section to manage.</p>;
    }
  };

  return (
    <div className="container mt-5">
      <h1 className="mb-4">Admin Panel</h1>
      {/* Navigation tabs to switch between different management sections */}
      <nav>
        <ul className="nav nav-tabs">
          <li className="nav-item">
            <button
              className={`nav-link ${activeSection === "coffees" ? "active" : ""}`}
              onClick={() => setActiveSection("coffees")}
            >
              Manage Coffees
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeSection === "roasteries" ? "active" : ""}`}
              onClick={() => setActiveSection("roasteries")}
            >
              Manage Roasteries
            </button>
          </li>
          <li className="nav-item">
          <button
            className={`nav-link ${activeSection === "users" ? "active" : ""}`}
            onClick={() => setActiveSection("users")}
          >
            Manage Users
          </button>
          </li>
        </ul>
      </nav>
      {/* Render the active section */}
      <div className="mt-4">{renderActiveSection()}</div>
    </div>
  );
};

export default AdminPage;