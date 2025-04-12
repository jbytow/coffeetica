import React, { useState, useEffect } from "react";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import apiClient from "../../../../lib/api";
import { Link } from "react-router-dom";
import { Pagination } from "../../../Utils/ui/Pagination";
import { SpinnerLoading } from "../../../Utils/ui/SpinnerLoading";
import RoasteryFilterPanel from "../../../Utils/filters/RoasteryFilterPanel";
import { RoasteryFilters } from "../../../../models/RoasteryFilters";

const ManageRoasteries: React.FC = () => {
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

  const handleDeleteRoastery = async (id: number) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this roastery?");
    if (!confirmDelete) return;

    setIsLoading(true);
    try {
      await apiClient.delete(`/roasteries/${id}`);
      setRoasteries((prev) => prev.filter((roastery) => roastery.id !== id));

      if (roasteries.length === 1 && currentPage > 1) {
        setCurrentPage((prev) => prev - 1);
      }
      setHttpError(null);
    } catch (err: any) {
      setHttpError("Failed to delete roastery");
    } finally {
      setIsLoading(false);
    }
  };

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
    <div>
      {httpError && <p className="text-danger">{httpError}</p>}
      <div className="mb-3 d-flex justify-content-between align-items-center">
        <Link to="/admin/roasteries/add" className="btn btn-primary">
          Add New Roastery
        </Link>
        <div className="text-end">
          <h5 className="mb-0">Number of results: ({totalResults})</h5>
          <p className="mb-0">
            {indexOfFirstItem + 1} to {lastItem} of {totalResults} items
          </p>
        </div>
      </div>
      <RoasteryFilterPanel filters={filters} onFiltersSubmit={handleFiltersSubmit} />
      {roasteries.map((roastery) => (
        <div
          className="card mt-3 shadow p-3 mb-3 bg-body rounded"
          key={roastery.id}
        >
          <div className="row g-0">
            <div className="col-md-3 d-flex justify-content-center align-items-center">
              <div>
                {roastery.imageUrl ? (
                  <img
                    src={`${import.meta.env.VITE_API_BASE_URL}${roastery.imageUrl}`}
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
            <div className="col-md-9">
              <div className="card-body">
                <div className="row align-items-center mb-2">
                  <div className="col-md-5">
                    <h5 className="card-title fs-4">{roastery.name}</h5>
                  </div>
                  <div className="col-md-5 d-flex justify-content-start">
                    <Link
                      to={`/admin/roasteries/edit/${roastery.id}`}
                      className="btn btn-secondary me-2"
                    >
                      Edit
                    </Link>
                    <button
                      className="btn btn-danger"
                      onClick={() => handleDeleteRoastery(roastery.id)}
                    >
                      Delete
                    </button>
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
                    <p className="card-text">
                      <strong>Website:</strong>{" "}
                      <a href={roastery.websiteUrl}>{roastery.websiteUrl}</a>
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

export default ManageRoasteries;