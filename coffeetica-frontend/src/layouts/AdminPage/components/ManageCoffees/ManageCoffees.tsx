import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import apiClient from "../../../../lib/api";
import { Link } from "react-router-dom";

const ManageCoffees: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [error, setError] = useState<string | null>(null);

  // Fetch all coffees
  useEffect(() => {
    const fetchCoffees = async () => {
      try {
        const response = await apiClient.get<CoffeeDTO[]>("/coffees");
        setCoffees(response.data);
      } catch (err: any) {
        console.error("Error fetching coffees:", err.response || err.message);
        setError("Failed to fetch coffees");
      }
    };

    fetchCoffees();
  }, []);

  // Handle deleting a coffee
  const handleDeleteCoffee = async (id: number) => {
    try {
      await apiClient.delete(`/coffees/${id}`);
      setCoffees((prev) => prev.filter((coffee) => coffee.id !== id));
    } catch (err: any) {
      console.error("Error deleting coffee:", err.response || err.message);
      setError("Failed to delete coffee");
    }
  };

  return (
    <div>
      <h2>Manage Coffees</h2>
      {error && <p className="text-danger">{error}</p>}
      <div className="mb-3">
        <Link to="/admin/coffees/add" className="btn btn-primary">
          Add New Coffee
        </Link>
      </div>
      <div>
        <h3>Existing Coffees</h3>
        <ul>
          {coffees.map((coffee) => (
            <li key={coffee.id}>
              <strong>{coffee.name}</strong> <br />
              Roast Level: {coffee.roastLevel} <br />
              {coffee.imageUrl && (
                <img
                  src={`http://localhost:8080${coffee.imageUrl}`}
                  alt={`${coffee.name} image`}
                  width="100"
                />
              )}
              <div className="mt-2">
                <button
                  className="btn btn-danger me-2"
                  onClick={() => handleDeleteCoffee(coffee.id)}
                >
                  Delete
                </button>
                <Link
                  to={`/admin/coffees/edit/${coffee.id}`}
                  className="btn btn-secondary"
                >
                  Edit
                </Link>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default ManageCoffees;