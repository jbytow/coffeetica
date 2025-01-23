import React, { useEffect, useState } from 'react';
import apiClient from '../../lib/api';
import { CoffeeFilters } from '../../models/CofffeeFilters';
import { RoasteryDTO } from '../../models/RoasteryDTO';

interface CoffeeFilterProps {
    filters: CoffeeFilters;
    onFiltersSubmit: (filters: CoffeeFilters) => void;
  }
  

  const CoffeeFilterPanel: React.FC<CoffeeFilterProps> = ({ filters, onFiltersSubmit }) => {
    const [localFilters, setLocalFilters] = useState<CoffeeFilters>(filters);
  
    // Stany na opcje pobrane z API
    const [regions, setRegions] = useState<string[]>([]);
    const [roastLevels, setRoastLevels] = useState<string[]>([]);
    const [flavorProfiles, setFlavorProfiles] = useState<string[]>([]);
    const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
    const [error, setError] = useState<string | null>(null);
  
    // Pobieranie opcji z API
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
  
    const handleInputChange = (key: keyof CoffeeFilters, value: string | number) => {
      setLocalFilters((prev) => ({ ...prev, [key]: value }));
    };
  
    const handleSearch = () => {
      onFiltersSubmit(localFilters);
    };
  
    return (
      <div className="filter-panel mb-4">
        {error && <p className="text-danger">{error}</p>}
        <div className="row">
          <div className="col-md-3">
            <input
              type="text"
              className="form-control"
              placeholder="Name"
              value={localFilters.name}
              onChange={(e) => handleInputChange("name", e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <input
              type="text"
              className="form-control"
              placeholder="Country of Origin"
              value={localFilters.countryOfOrigin}
              onChange={(e) => handleInputChange("countryOfOrigin", e.target.value)}
            />
          </div>
          <div className="col-md-3">
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
          <div className="col-md-3">
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
        </div>
        <div className="row mt-3">
          <div className="col-md-3">
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
          <div className="col-md-3">
            <input
              type="text"
              className="form-control"
              placeholder="Processing Method"
              value={localFilters.processingMethod}
              onChange={(e) => handleInputChange("processingMethod", e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <input
              type="text"
              className="form-control"
              placeholder="Flavor Notes"
              value={localFilters.flavorNotes}
              onChange={(e) => handleInputChange("flavorNotes", e.target.value)}
            />
          </div>
          <div className="col-md-3">
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
        </div>
        <div className="row mt-3">
          <div className="col-md-3">
            <input
              type="number"
              className="form-control"
              placeholder="Min Production Year"
              value={localFilters.minProductionYear}
              onChange={(e) => handleInputChange("minProductionYear", parseInt(e.target.value))}
            />
          </div>
          <div className="col-md-3">
            <input
              type="number"
              className="form-control"
              placeholder="Max Production Year"
              value={localFilters.maxProductionYear}
              onChange={(e) => handleInputChange("maxProductionYear", parseInt(e.target.value))}
            />
          </div>
          <div className="col-md-3 d-flex align-items-end">
            <button
              className="btn btn-secondary w-100"
              onClick={handleSearch}
            >
              Search
            </button>
          </div>
        </div>
      </div>
    );
  };
  
  export default CoffeeFilterPanel;