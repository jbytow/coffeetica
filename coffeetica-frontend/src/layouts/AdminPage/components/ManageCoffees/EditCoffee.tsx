import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../../../../lib/api";

import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import { SpinnerLoading } from "../../../Utils/SpinnerLoading";
import SearchableDropdown from "../../../Utils/SearchableDropdown";

const EditCoffee: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
  
    const [coffee, setCoffee] = useState<Partial<CoffeeDTO>>({});
    const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
    const [regions, setRegions] = useState<string[]>([]);
    const [roastLevels, setRoastLevels] = useState<string[]>([]);
    const [flavorProfiles, setFlavorProfiles] = useState<string[]>([]);
    
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);


  // Fetch options for dropdowns
  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [regionsResponse, roastLevelsResponse, flavorProfilesResponse] = await Promise.all([
          apiClient.get<string[]>("/coffees/options/regions"),
          apiClient.get<string[]>("/coffees/options/roast-levels"),
          apiClient.get<string[]>("/coffees/options/flavor-profiles"),
        ]);
        setRegions(regionsResponse.data);
        setRoastLevels(roastLevelsResponse.data);
        setFlavorProfiles(flavorProfilesResponse.data);
      } catch (err: any) {
        console.error("Error fetching options:", err.response || err.message);
        setError("Failed to fetch coffee options.");
      }
    };

    fetchOptions();
  }, []);

  // Fetch coffee details and roasteries
  useEffect(() => {
    const fetchData = async () => {
      try {
        const coffeeResponse = await apiClient.get<CoffeeDTO>(`/coffees/${id}`);
        setCoffee(coffeeResponse.data);

        const roasteriesResponse = await apiClient.get<RoasteryDTO[]>("/roasteries");
        setRoasteries(roasteriesResponse.data);
      } catch (err: any) {
        setError("Failed to fetch coffee details or roasteries.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [id]);

  // Handle form submission
  const handleUpdateCoffee = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      if (!coffee.name || !coffee.countryOfOrigin || !coffee.roastLevel || !coffee.flavorProfile) {
        setError("All required fields must be filled.");
        return;
      }

      await apiClient.put<CoffeeDTO>(`/coffees/${id}`, coffee);

      if (selectedFile) {
        const formData = new FormData();
        formData.append("file", selectedFile);
        await apiClient.post(`/coffees/${id}/upload-image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      navigate("/admin/coffees");
    } catch (err: any) {
      setError("Failed to update coffee.");
    }
  };

  // Render loading spinner
  if (isLoading) return <SpinnerLoading />;

  return (
    <div className="container mt-5 mb-5">
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="card">
        <div className="card-header">Edit Coffee</div>
        <div className="card-body">
          <form onSubmit={handleUpdateCoffee}>
            <div className="row">
              {/* Name - Flavor Profile */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={coffee.name || ""}
                  onChange={(e) => setCoffee({ ...coffee, name: e.target.value })}
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={flavorProfiles}
                  label="Flavor Profile"
                  value={coffee.flavorProfile || ""}
                  onChange={(value) => setCoffee({ ...coffee, flavorProfile: value })}
                />
              </div>

              {/* Roast Level - Roastery */}
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={roastLevels}
                  label="Roast Level"
                  value={coffee.roastLevel || ""}
                  onChange={(value) => setCoffee({ ...coffee, roastLevel: value })}
                />
              </div>
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={roasteries.map((roastery) => roastery.name)}
                  label="Roastery"
                  value={coffee.roastery?.name || ""}
                  onChange={(value) => {
                    const selectedRoastery = roasteries.find((r) => r.name === value);
                    setCoffee({ ...coffee, roastery: selectedRoastery });
                  }}
                />
              </div>

              {/* Region - Notes */}
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={regions}
                  label="Region"
                  value={coffee.region || ""}
                  onChange={(value) => setCoffee({ ...coffee, region: value })}
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Notes</label>
                <input
                  type="text"
                  className="form-control"
                  value={coffee.notes || ""}
                  onChange={(e) => setCoffee({ ...coffee, notes: e.target.value })}
                  required
                />
              </div>

              {/* Country of Origin - Processing Method */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Country of Origin</label>
                <input
                  type="text"
                  className="form-control"
                  value={coffee.countryOfOrigin || ""}
                  onChange={(e) =>
                    setCoffee({ ...coffee, countryOfOrigin: e.target.value })
                  }
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Processing Method</label>
                <input
                  type="text"
                  className="form-control"
                  value={coffee.processingMethod || ""}
                  onChange={(e) =>
                    setCoffee({ ...coffee, processingMethod: e.target.value })
                  }
                  required
                />
              </div>

              {/* Image - Production Year */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Image</label>
                <input
                  type="file"
                  className="form-control"
                  accept="image/*"
                  onChange={(e) =>
                    setSelectedFile(e.target.files ? e.target.files[0] : null)
                  }
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Production Year</label>
                <input
                  type="number"
                  className="form-control"
                  value={coffee.productionYear || ""}
                  onChange={(e) =>
                    setCoffee({
                      ...coffee,
                      productionYear: parseInt(e.target.value, 10),
                    })
                  }
                  required
                />
              </div>
            </div>
            <button type="submit" className="btn btn-primary">
              Update Coffee
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditCoffee;