import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import { CoffeeFilters } from "../../models/CofffeeFilters";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/ui/SpinnerLoading";
import CoffeeFilterPanel from "../Utils/filters/CoffeeFilterPanel";
import { Pagination } from "../Utils/ui/Pagination";
import { Link, useLocation, useNavigate } from "react-router-dom";

const CoffeesListPage: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  
  const [currentPage, setCurrentPage] = useState(1);
  const [coffeesPerPage] = useState(5);
  
  const [totalPages, setTotalPages] = useState(0);
  const [totalResults, setTotalResults] = useState(0);

  const [filters, setFilters] = useState<CoffeeFilters>({
    name: "",
    countryOfOrigin: "",
    region: "",
    roastLevel: "",
    flavorProfile: "",
    flavorNotes: "",
    processingMethod: "",
    minProductionYear: "",
    maxProductionYear: "",
    roasteryName: "",
  });

  const [initialized, setInitialized] = useState(false);

  const location = useLocation();
  const navigate = useNavigate();

  // -------------------- EFFECT A: Read parameter from URL (only once) --------------------
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const roasteryParam = searchParams.get("roasteryName");

    if (roasteryParam) {
      // If the user accessed the page with a ?roasteryName=XYZ link
      setFilters((prev) => ({ ...prev, roasteryName: roasteryParam }));
    }

    // Mark that the parameter has been read
    setInitialized(true);
    // This effect will run ONLY once because it has no dependencies 
  }, []); 

  // -------------------- EFFECT B: Fetch coffees when filters/page changes --------------------
  useEffect(() => {
    // Avoid fetching until the parameter from the URL is initialized
    // to prevent the "first" request with an empty filter.
    if (!initialized) {
      return;
    }

    const fetchCoffees = async () => {
      setIsLoading(true);
      try {
        const response = await apiClient.get("/coffees", {
          params: {
            ...filters,
            page: currentPage - 1,
            size: coffeesPerPage,
          },
        });
        setCoffees(response.data.content);
        setTotalPages(response.data.totalPages);
        setTotalResults(response.data.totalElements);
        setHttpError(null);
      } catch (err: any) {
        setHttpError("Failed to fetch coffees");
      } finally {
        setIsLoading(false);
      }
    };

    fetchCoffees();
  }, [initialized, filters, currentPage, coffeesPerPage]);

  const handleFiltersSubmit = (newFilters: CoffeeFilters) => {
    setFilters(newFilters);
    setCurrentPage(1);
  };

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  const indexOfFirstItem = (currentPage - 1) * coffeesPerPage;
  const lastItem = Math.min(indexOfFirstItem + coffees.length, totalResults);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  return (
    <div className="container mt-5">
      {httpError && <p className="text-danger">{httpError}</p>}

      {/* Coffee filters */}
      <CoffeeFilterPanel
        filters={filters}
        onFiltersSubmit={handleFiltersSubmit}
        onClearFilters={() => {
          navigate("/coffees", { replace: true });
        }}
      />

      {/* Number of results */}
      <div className="mt-3">
        <h5 className="mb-0">Number of results: ({totalResults})</h5>
        <p className="mb-0">
          {indexOfFirstItem + 1} to {lastItem} of {totalResults} items
        </p>
      </div>

      {/* Coffee cards */}
      {coffees.map((coffee) => (
        <div
          className="card mt-3 shadow p-3 mb-3 bg-body rounded"
          key={coffee.id}
        >
          <div className="row g-0">
            {/* Coffee image */}
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

            {/* Coffee details */}
            <div className="col-md-9">
              <div className="card-body">
                <div className="row align-items-center mb-2">
                  <div className="col-md-5">
                    <Link to={`/coffees/${coffee.id}`} className="fs-4 text-decoration-none">
                      <h5 className="card-title">{coffee.name}</h5>
                    </Link>
                  </div>
                </div>
                <div className="row">
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
                  <div className="col-md-5">
                    <p className="card-text">
                      <strong>Roast Level:</strong> {coffee.roastLevel}
                    </p>
                    <p className="card-text">
                      <strong>Flavor Profile:</strong> {coffee.flavorProfile}
                    </p>
                    <p className="card-text">
                      <strong>Flavor Notes:</strong> {coffee.flavorNotes.join(", ")}
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

      {/* Pagination */}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          paginate={paginate}
        />
      )}
    </div>
  );
};

export default CoffeesListPage;