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
      {error && <p className="text-danger">{error}</p>}
      <div className="mb-3 d-flex align-items-center">
        <Link to="/admin/coffees/add" className="btn btn-primary me-3">
          Add New Coffee
        </Link>
      </div>
      {coffees.map((coffee) => (
        <div className="card mt-3 shadow p-3 mb-3 bg-body rounded" key={coffee.id}>
          <div className="row g-0">
            {/* Obraz */}
            <div className="col-md-3 d-flex justify-content-center align-items-center">
              <div>
                {coffee.imageUrl ? (
                  <img
                    src={`http://localhost:8080${coffee.imageUrl}`}
                    width="123"
                    height="196"
                    alt={coffee.name}
                  />
                ) : (
                  <img
                    src="https://via.placeholder.com/123x196"
                    width="123"
                    height="196"
                    alt="Coffee placeholder"
                  />
                )}
              </div>
            </div>
            {/* Szczegóły kawy */}
            <div className="col-md-9">
              <div className="card-body">
                <div className="row align-items-center mb-2">
                  <div className="col-md-5">
                    <h5 className="card-title fs-4">{coffee.name}</h5>
                  </div>
                  <div className="col-md-5 d-flex justify-content-start">
                    <Link
                      to={`/admin/coffees/edit/${coffee.id}`}
                      className="btn btn-secondary me-2"
                    >
                      Edit
                    </Link>
                    <button
                      className="btn btn-danger"
                      onClick={() => handleDeleteCoffee(coffee.id)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
                <div className="row">
                  {/* Kolumna 1 */}
                  <div className="col-md-5">
                    <p className="card-text">
                      <strong>Roastery:</strong> {coffee.roastery.name}
                    </p>
                    <p className="card-text">
                      <strong>Region:</strong> {coffee.region}
                    </p>
                    <p className="card-text">
                      <strong>Country of Origin:</strong> {coffee.countryOfOrigin}
                    </p>
                    <p className="card-text">
                      <strong>Production Year:</strong> {coffee.productionYear}
                    </p>
                  </div>
                  {/* Kolumna 2 */}
                  <div className="col-md-5">
                    <p className="card-text">
                      <strong>Roast Level:</strong> {coffee.roastLevel}
                    </p>
                    <p className="card-text">
                      <strong>Flavor Profile:</strong> {coffee.flavorProfile}
                    </p>
                    <p className="card-text">
                      <strong>Flavor Notes:</strong>{" "}
                      {coffee.flavorNotes.join(", ")}
                    </p>
                    <p className="card-text">
                      <strong>Processing Method:</strong> {coffee.processingMethod}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ManageCoffees;