import React, { useState } from "react";
import { RoasteryFilters } from "../../../models/RoasteryFilters";

interface RoasteryFilterProps {
  filters: RoasteryFilters;
  onFiltersSubmit: (filters: RoasteryFilters) => void;
}

const RoasteryFilterPanel: React.FC<RoasteryFilterProps> = ({
  filters,
  onFiltersSubmit,
}) => {
  const [localFilters, setLocalFilters] = useState<RoasteryFilters>(filters);

  const handleInputChange = (key: keyof RoasteryFilters, value: string | number) => {
    setLocalFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleSearch = () => {
    onFiltersSubmit(localFilters);
  };

  const handleClearFilters = () => {
    const defaultFilters: RoasteryFilters = {
      name: "",
      country: "",
      minFoundingYear: "",
      maxFoundingYear: "",
    };
    setLocalFilters(defaultFilters);
    onFiltersSubmit(defaultFilters);
  };

  return (
    <div className="filter-panel mb-4">
      <div className="row g-3">
        {/* Name */}
        <div className="col-xl-2 col-lg-2 col-md-6 col-sm-12">
          <input
            type="text"
            className="form-control"
            placeholder="Name"
            value={localFilters.name}
            onChange={(e) => handleInputChange("name", e.target.value)}
          />
        </div>

        {/* Country */}
        <div className="col-xl-2 col-lg-2 col-md-6 col-sm-12">
          <input
            type="text"
            className="form-control"
            placeholder="Country"
            value={localFilters.country}
            onChange={(e) => handleInputChange("country", e.target.value)}
          />
        </div>

        {/* Min Founding Year */}
        <div className="col-xl-2 col-lg-2 col-md-6 col-sm-12">
          <input
            type="number"
            className="form-control"
            placeholder="Min Founding Year"
            value={localFilters.minFoundingYear || ""}
            onChange={(e) =>
              handleInputChange("minFoundingYear", parseInt(e.target.value, 10))
            }
          />
        </div>

        {/* Max Founding Year */}
        <div className="col-xl-2 col-lg-2 col-md-6 col-sm-12">
          <input
            type="number"
            className="form-control"
            placeholder="Max Founding Year"
            value={localFilters.maxFoundingYear || ""}
            onChange={(e) =>
              handleInputChange("maxFoundingYear", parseInt(e.target.value, 10))
            }
          />
        </div>

        {/* Buttons */}
        <div className="col-xl-4 col-lg-4 col-md-12 col-sm-12 d-flex justify-content-end align-items-center">
          <button
            className="btn btn-outline-secondary me-2"
            onClick={handleClearFilters}
          >
            <i className="bi bi-x-lg me-2"></i> Clear Filters
          </button>
          <button className="btn btn-light" onClick={handleSearch}>
            <i className="bi bi-search" style={{ fontSize: "16px", color: "#000" }}></i>
          </button>
        </div>
      </div>
    </div>
  );
};


export default RoasteryFilterPanel;