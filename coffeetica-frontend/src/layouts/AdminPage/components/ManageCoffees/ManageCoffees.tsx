import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import apiClient from "../../../../lib/api";
import { Link } from "react-router-dom";
import { Pagination } from "../../../Utils/Pagination";
import { SpinnerLoading } from "../../../Utils/SpinnerLoading";

const ManageCoffees: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [coffeesPerPage] = useState(5);
  const [totalPages, setTotalPages] = useState(0);

  // Fetch all coffees
  useEffect(() => {
    const fetchCoffees = async () => {
      setIsLoading(true);
      try {
        const response = await apiClient.get("/coffees", {
          params: {
            page: currentPage - 1, // Backend paginates from 0
            size: coffeesPerPage,
          },
        });

        setCoffees(response.data.content);
        setTotalPages(response.data.totalPages);
        setHttpError(null);
      } catch (err: any) {
        console.error("Error fetching coffees:", err.response || err.message);
        setHttpError("Failed to fetch coffees");
      } finally {
        setIsLoading(false);
      }
    };

    fetchCoffees();
  }, [currentPage]);

  // Handle deleting a coffee
  const handleDeleteCoffee = async (id: number) => {
    setIsLoading(true);
    try {
      await apiClient.delete(`/coffees/${id}`);
      setCoffees((prev) => prev.filter((coffee) => coffee.id !== id));

      if (coffees.length === 1 && currentPage > 1) {
        setCurrentPage((prev) => prev - 1);
      }
      setHttpError(null);
    } catch (err: any) {
      console.error("Error deleting coffee:", err.response || err.message);
      setHttpError("Failed to delete coffee");
    } finally {
      setIsLoading(false);
    }
  };

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  if (isLoading) {
    return (
      <SpinnerLoading />
    )
  }

  return (
    <div>
      {httpError && <p className="text-danger">{httpError}</p>}
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
            {/* Coffee Details */}
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
                  {/* Column 1 */}
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
                  {/* Column 2 */}
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
      {totalPages > 1 && <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />}
    </div>
  );
};

export default ManageCoffees;