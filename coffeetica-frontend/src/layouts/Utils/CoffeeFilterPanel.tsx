import React, { useEffect, useState } from 'react';
import apiClient from '../../lib/api';
import { CoffeeFilters } from '../../models/CofffeeFilters';
import { RoasteryDTO } from '../../models/RoasteryDTO';

interface CoffeeFilterProps {
  filters: CoffeeFilters;
  onFiltersSubmit: (filters: CoffeeFilters) => void;
  onClearFilters?: () => void;
}

const CoffeeFilterPanel: React.FC<CoffeeFilterProps> = ({ filters, onFiltersSubmit, onClearFilters }) => {
  const [localFilters, setLocalFilters] = useState<CoffeeFilters>(filters);
  const [showMore, setShowMore] = useState<boolean>(false);

  // States for API options
  const [regions, setRegions] = useState<string[]>([]);
  const [roastLevels, setRoastLevels] = useState<string[]>([]);
  const [flavorProfiles, setFlavorProfiles] = useState<string[]>([]);
  const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
  const [error, setError] = useState<string | null>(null);

  // Download options from API
  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [regionsResponse, roastLevelsResponse, flavorProfilesResponse, roasteriesResponse] =
          await Promise.all([
            apiClient.get<string[]>("/coffees/options/regions"),
            apiClient.get<string[]>("/coffees/options/roast-levels"),
            apiClient.get<string[]>("/coffees/options/flavor-profiles"),
            apiClient.get<RoasteryDTO[]>("/roasteries"),
          ]);

        setRegions(regionsResponse.data);
        setRoastLevels(roastLevelsResponse.data);
        setFlavorProfiles(flavorProfilesResponse.data);
        setRoasteries(roasteriesResponse.data);
      } catch (err: any) {
        console.error("Error fetching options:", err.response || err.message);
        setError("Failed to fetch coffee options.");
      }
    };

    fetchOptions();
  }, []);

  useEffect(() => {
    setLocalFilters(filters);
  }, [filters]);

  const handleInputChange = (key: keyof CoffeeFilters, value: string | number) => {
    setLocalFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleSearch = () => {
    onFiltersSubmit(localFilters);
  };

  const handleClearFilters = () => {
    const defaultFilters: CoffeeFilters = {
      name: '',
      countryOfOrigin: '',
      region: '',
      roastLevel: '',
      flavorProfile: '',
      flavorNotes: '',
      processingMethod: '',
      minProductionYear: '',
      maxProductionYear: '',
      roasteryName: '',
    };
    setLocalFilters(defaultFilters);
    onFiltersSubmit(defaultFilters);

    if (onClearFilters) {
      onClearFilters();
    }
  };

  return (
    <div className="filter-panel mb-4">
      {error && <p className="text-danger">{error}</p>}

      {/* Main filters + Show More and Search */}

      <div className="row g-3">
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
          <input
            type="text"
            className="form-control"
            placeholder="Name"
            value={localFilters.name}
            onChange={(e) => handleInputChange("name", e.target.value)}
          />
        </div>
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
          <select
            className="form-select"
            value={localFilters.roasteryName}
            onChange={(e) => handleInputChange("roasteryName", e.target.value)}
          >
            <option value="">All Roasteries</option>
            {roasteries.map((roastery) => (
              <option key={roastery.id} value={roastery.name}>
                {roastery.name}
              </option>
            ))}
          </select>
        </div>
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
          <select
            className="form-select"
            value={localFilters.region}
            onChange={(e) => handleInputChange("region", e.target.value)}
          >
            <option value="">All Regions</option>
            {regions.map((region) => (
              <option key={region} value={region}>
                {region}
              </option>
            ))}
          </select>
        </div>
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
          <select
            className="form-select"
            value={localFilters.roastLevel}
            onChange={(e) => handleInputChange("roastLevel", e.target.value)}
          >
            <option value="">All Roast Levels</option>
            {roastLevels.map((level) => (
              <option key={level} value={level}>
                {level}
              </option>
            ))}
          </select>
        </div>
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
          <select
            className="form-select"
            value={localFilters.flavorProfile}
            onChange={(e) => handleInputChange("flavorProfile", e.target.value)}
          >
            <option value="">All Flavor Profiles</option>
            {flavorProfiles.map((profile) => (
              <option key={profile} value={profile}>
                {profile}
              </option>
            ))}
          </select>
        </div>

        {/* Last column */}
        <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12 d-flex align-items-center justify-content-between">
          <button
            className="btn btn-link text-decoration-none text-reset text-nowrap fw-bold d-flex align-items-center"
            onClick={() => setShowMore(!showMore)}
          >
            {showMore ? (
              <>
                Show Less <i className="bi bi-chevron-up ms-1"></i>
              </>
            ) : (
              <>
                Show More <i className="bi bi-chevron-down ms-1"></i>
              </>
            )}
          </button>
          <button className="btn btn-light" onClick={handleSearch}>
            <i className="bi bi-search" style={{ fontSize: '16px', color: '#000' }}></i>
          </button>
        </div>
      </div>

      {/* Show more filters */}
      {showMore && (
        <div className="row g-3 mt-2">
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
            <input
              type="text"
              className="form-control"
              placeholder="Country of Origin"
              value={localFilters.countryOfOrigin}
              onChange={(e) => handleInputChange("countryOfOrigin", e.target.value)}
            />
          </div>
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
            <input
              type="text"
              className="form-control"
              placeholder="Flavor Notes"
              value={localFilters.flavorNotes}
              onChange={(e) => handleInputChange("flavorNotes", e.target.value)}
            />
          </div>
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
            <input
              type="text"
              className="form-control"
              placeholder="Processing Method"
              value={localFilters.processingMethod}
              onChange={(e) => handleInputChange("processingMethod", e.target.value)}
            />
          </div>
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
            <input
              type="number"
              className="form-control"
              placeholder="Min Production Year"
              value={localFilters.minProductionYear}
              onChange={(e) => handleInputChange("minProductionYear", parseInt(e.target.value))}
            />
          </div>
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12">
            <input
              type="number"
              className="form-control"
              placeholder="Max Production Year"
              value={localFilters.maxProductionYear}
              onChange={(e) => handleInputChange("maxProductionYear", e.target.value)}
            />
          </div>
          <div className="col-xl-2 col-lg-4 col-md-4 col-sm-6 col-12 d-flex align-items-center justify-content-end">
            <button
              className="btn btn-outline-secondary"
              onClick={handleClearFilters}
            >
              <i className="bi bi-x-lg me-2"></i> Clear Filters
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CoffeeFilterPanel;