import React, { useState, useEffect } from "react";
import { RoasteryDTO } from "../../models/RoasteryDTO";
import { RoasteryFilters } from "../../models/RoasteryFilters";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import RoasteryFilterPanel from "../Utils/RoasteryFilterPanel";
import { Pagination } from "../Utils/Pagination";
import { Link } from "react-router-dom";

const RoasteriesListPage: React.FC = () => {
  const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [roasteriesPerPage] = useState(5);
  const [totalPages, setTotalPages] = useState(0);
  const [totalResults, setTotalResults] = useState(0);

  const [filters, setFilters] = useState<RoasteryFilters>({
    name: "",
    country: "",
    minFoundingYear: "",
    maxFoundingYear: "",
  });

  useEffect(() => {
    const fetchRoasteries = async () => {
      setIsLoading(true);
      try {
        const response = await apiClient.get("/roasteries/filter", {
          params: {
            ...filters,
            page: currentPage - 1,
            size: roasteriesPerPage,
          },
        });
        setRoasteries(response.data.content);
        setTotalPages(response.data.totalPages);
        setTotalResults(response.data.totalElements);
        setHttpError(null);
      } catch (err: any) {
        setHttpError("Failed to fetch roasteries");
      } finally {
        setIsLoading(false);
      }
    };
    fetchRoasteries();
  }, [filters, currentPage]);

  const handleFiltersSubmit = (newFilters: RoasteryFilters) => {
    setFilters(newFilters);
    setCurrentPage(1);
  };

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  const indexOfFirstItem = (currentPage - 1) * roasteriesPerPage;
  const lastItem = Math.min(indexOfFirstItem + roasteries.length, totalResults);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  return (
    <div className="container mt-5">
      {httpError && <p className="text-danger">{httpError}</p>}

      {/* Roastery filters */}
      <RoasteryFilterPanel filters={filters} onFiltersSubmit={handleFiltersSubmit} />

      {/* Number of results */}
      <div className="mt-3">
        <h5 className="mb-0">Number of results: ({totalResults})</h5>
        <p className="mb-0">
          {indexOfFirstItem + 1} to {lastItem} of {totalResults} items
        </p>
      </div>

      {/* Roastery cards */}
      {roasteries.map((roastery) => (
        <div
          className="card mt-3 shadow p-3 mb-3 bg-body rounded"
          key={roastery.id}
        >
          <div className="row g-0">
            {/* Roastery image */}
            <div className="col-md-3 d-flex justify-content-center align-items-center">
              <div>
                {roastery.imageUrl ? (
                  <img
                    src={`http://localhost:8080${roastery.imageUrl}`}
                    width="123"
                    height="196"
                    alt={roastery.name}
                  />
                ) : (
                  <img
                    src="https://via.placeholder.com/123x196"
                    width="123"
                    height="196"
                    alt="Roastery placeholder"
                  />
                )}
              </div>
            </div>

            {/* Roastery details */}
            <div className="col-md-9">
              <div className="card-body">
                <div className="row align-items-center mb-2">
                  <div className="col-md-5">
                    <Link
                      to={`/roasteries/${roastery.id}/coffees`}
                      className="fs-4 text-decoration-none"
                    >
                      <h5 className="card-title">{roastery.name}</h5>
                    </Link>
                  </div>
                </div>
                <div className="row">
                  <div className="col-md-5">
                    <p className="card-text">
                      <strong>Country:</strong> {roastery.country}
                    </p>
                    <p className="card-text">
                      <strong>Founding Year:</strong> {roastery.foundingYear}
                    </p>
                  </div>
                  <div className="col-md-5">
                    <p className="card-text">
                      <strong>Website:</strong>{" "}
                      <a href={roastery.websiteUrl} target="_blank" rel="noopener noreferrer">
                        {roastery.websiteUrl}
                      </a>
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

export default RoasteriesListPage;