import React, { useState } from "react";
import ManageCoffees from "./components/ManageCoffees";
import ManageRoasteries from "./components/ManageRoasteries";
// In the future, you can add more sections like ManageUsers
// import ManageUsers from "./ManageUsers";

const AdminPage: React.FC = () => {
  // State to keep track of the currently active section
  const [activeSection, setActiveSection] = useState("coffees");

  // Function to render the appropriate section based on the active state
  const renderActiveSection = () => {
    switch (activeSection) {
      case "coffees":
        return <ManageCoffees />;
      case "roasteries":
        return <ManageRoasteries />;
      // Uncomment and add more cases for additional sections in the future
      // case "users":
      //   return <ManageUsers />;
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
          {/* Add more tabs here for additional sections */}
          {/* <li className="nav-item">
            <button
              className={`nav-link ${activeSection === "users" ? "active" : ""}`}
              onClick={() => setActiveSection("users")}
            >
              Manage Users
            </button>
          </li> */}
        </ul>
      </nav>
      {/* Render the active section */}
      <div className="mt-4">{renderActiveSection()}</div>
    </div>
  );
};

export default AdminPage;